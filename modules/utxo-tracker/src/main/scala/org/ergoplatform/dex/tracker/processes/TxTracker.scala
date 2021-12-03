package org.ergoplatform.dex.tracker.processes

import cats.{Defer, Monad, MonoidK}
import derevo.derive
import org.ergoplatform.dex.tracker.configs.TrackerConfig
import org.ergoplatform.dex.tracker.handlers.TxHandler
import org.ergoplatform.dex.tracker.repositories.TrackerCache
import org.ergoplatform.ergo.ErgoNetworkStreaming
import tofu.Catches
import tofu.higherKind.derived.representableK
import tofu.logging.Logging
import tofu.streams.{Evals, Pace, ParFlatten}
import tofu.syntax.handle._
import tofu.syntax.logging._
import tofu.syntax.monadic._
import tofu.syntax.streams.all._

@derive(representableK)
trait TxTracker[F[_]] {

  def run: F[Unit]
}

object TxTracker {

  final class StreamingTxTracker[
    F[_]: Monad: Evals[*[_], G]: ParFlatten: Pace: Defer: MonoidK: Catches,
    G[_]: Monad: Logging
  ](cache: TrackerCache[G], conf: TrackerConfig, handlers: List[TxHandler[F]])(implicit
    client: ErgoNetworkStreaming[F, G]
  ) extends TxTracker[F] {

    def run: F[Unit] =
      eval(info"Stating TX tracker ..") >>
      eval(cache.lastScannedTxOffset).repeat
        .flatMap { lastOffset =>
          eval(client.getNetworkInfo).flatMap { networkParams =>
            val offset     = lastOffset max conf.initialOffset
            val maxOffset  = networkParams.maxBoxGix
            val nextOffset = (offset + conf.batchSize) min maxOffset
            val scan =
              eval(info"Requesting TX batch {offset=$offset, maxOffset=$maxOffset, batchSize=${conf.batchSize} ..") >>
              client
                .streamTransactions(offset, conf.batchSize)
                .evalTap(tx => trace"Scanning TX $tx")
                .flatTap(tx => emits(handlers.map(_(tx.pure[F]))).parFlattenUnbounded)
                .evalMap(tx => cache.setLastScannedBoxOffset(tx.globalIndex))
            val finalizeOffset = eval(cache.setLastScannedTxOffset(nextOffset))
            val pause =
              eval(info"Upper limit {maxOffset=$maxOffset} was reached. Retrying in ${conf.retryDelay.toSeconds}s") >>
              unit[F].delay(conf.retryDelay)

            emits(if (offset != maxOffset) List(scan, finalizeOffset) else List(pause)).flatten
          }
        }
        .handleWith[Throwable] { e =>
          val delay = conf.retryDelay
          eval(warnCause"Tracker failed. Retrying in $delay ms" (e)) >> run.delay(delay)
        }
  }
}