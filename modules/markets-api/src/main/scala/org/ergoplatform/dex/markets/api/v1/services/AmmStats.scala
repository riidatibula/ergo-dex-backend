package org.ergoplatform.dex.markets.api.v1.services

import cats.Monad
import cats.data.OptionT
import mouse.anyf._
import org.ergoplatform.common.models.TimeWindow
import org.ergoplatform.dex.domain.amm.PoolId
import org.ergoplatform.dex.markets.api.v1.models.amm.{PlatformSummary, PoolSummary}
import org.ergoplatform.dex.markets.currencies.UsdUnits
import org.ergoplatform.dex.markets.domain.{Fees, TotalValueLocked, Volume}
import org.ergoplatform.dex.markets.modules.PriceSolver.FiatPriceSolver
import org.ergoplatform.dex.markets.repositories.Pools
import tofu.doobie.transactor.Txr
import mouse.anyf._
import cats.syntax.traverse._
import tofu.syntax.monadic._

trait AmmStats[F[_]] {

  def getPlatformSummary(window: TimeWindow): F[PlatformSummary]

  def getPoolSummary(poolId: PoolId, window: TimeWindow): F[Option[PoolSummary]]
}

object AmmStats {

  def make[F[_]: Monad, D[_]: Monad](implicit
    txr: Txr.Aux[F, D],
    pools: Pools[D],
    fiatSolver: FiatPriceSolver[F]
  ): AmmStats[F] = new Live[F, D]()

  final class Live[F[_]: Monad, D[_]: Monad](implicit
    txr: Txr.Aux[F, D],
    pools: Pools[D],
    fiatSolver: FiatPriceSolver[F]
  ) extends AmmStats[F] {

    def getPlatformSummary(window: TimeWindow): F[PlatformSummary] = {
      val statsQuery =
        for {
          poolSnapshots <- pools.snapshots
          volumes       <- pools.volumes(window)
        } yield (poolSnapshots, volumes)
      for {
        (poolSnapshots, volumes) <- statsQuery ||> txr.trans
        lockedX                  <- poolSnapshots.flatTraverse(pool => fiatSolver.convert(pool.lockedX, UsdUnits).map(_.toList))
        lockedY                  <- poolSnapshots.flatTraverse(pool => fiatSolver.convert(pool.lockedY, UsdUnits).map(_.toList))
        tvl = TotalValueLocked(lockedX.map(_.value).sum + lockedY.map(_.value).sum, UsdUnits)

        volumeByX <- volumes.flatTraverse(pool => fiatSolver.convert(pool.volumeByX, UsdUnits).map(_.toList))
        volumeByY <- volumes.flatTraverse(pool => fiatSolver.convert(pool.volumeByY, UsdUnits).map(_.toList))
        volume = Volume(volumeByX.map(_.value).sum + volumeByY.map(_.value).sum, UsdUnits, window)
      } yield PlatformSummary(tvl, volume)
    }

    def getPoolSummary(poolId: PoolId, window: TimeWindow): F[Option[PoolSummary]] = {
      val poolStatsQuery =
        (for {
          pool     <- OptionT(pools.snapshot(poolId))
          vol      <- OptionT(pools.volume(poolId, window))
          feesSnap <- OptionT(pools.fees(poolId, window))
        } yield (pool, vol, feesSnap)).value
      (for {
        (pool, vol, feesSnap) <- OptionT(poolStatsQuery ||> txr.trans)
        lockedX               <- OptionT(fiatSolver.convert(pool.lockedX, UsdUnits))
        lockedY               <- OptionT(fiatSolver.convert(pool.lockedY, UsdUnits))
        tvl = TotalValueLocked(lockedX.value + lockedY.value, UsdUnits)

        volX <- OptionT(fiatSolver.convert(vol.volumeByX, UsdUnits))
        volY <- OptionT(fiatSolver.convert(vol.volumeByY, UsdUnits))
        volume = Volume(volX.value + volY.value, UsdUnits, window)

        feesX <- OptionT(fiatSolver.convert(feesSnap.feesByX, UsdUnits))
        feesY <- OptionT(fiatSolver.convert(feesSnap.feesByY, UsdUnits))
        fees = Fees(feesX.value + feesY.value, UsdUnits, window)
      } yield PoolSummary(poolId, pool.lockedX, pool.lockedY, tvl, volume, fees)).value
    }
  }
}