package org.ergoplatform.dex.index.repos

import cats.data.NonEmptyList
import cats.tagless.syntax.functorK._
import cats.{FlatMap, Functor}
import derevo.derive
import doobie.ConnectionIO
import doobie.util.log.LogHandler
import org.ergoplatform.dex.index.db.models.DBAssetInfo
import org.ergoplatform.dex.index.sql.{AssetSql, CFMMPoolSql}
import tofu.doobie.LiftConnectionIO
import tofu.doobie.log.EmbeddableLogHandler
import tofu.higherKind.derived.representableK
import tofu.logging.Logs
import tofu.syntax.monadic._

@derive(representableK)
trait Assets[F[_]] {

  def insert(assets: NonEmptyList[DBAssetInfo]): F[Int]
}

object Assets {

  def make[I[_]: Functor, D[_]: FlatMap: LiftConnectionIO](implicit
    elh: EmbeddableLogHandler[D],
    logs: Logs[I, D]
  ): I[Assets[D]] =
    logs.forService[Assets[D]].map { implicit l =>
      elh.embed(implicit lh => new Live().mapK(LiftConnectionIO[D].liftF))
    }

  final class Live(implicit lh: LogHandler) extends Assets[ConnectionIO] {

    def insert(assets: NonEmptyList[DBAssetInfo]): ConnectionIO[Int] =
      AssetSql.insertNoConflict[DBAssetInfo].updateMany(assets)
  }
}
