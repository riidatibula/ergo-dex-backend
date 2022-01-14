package org.ergoplatform.dex.markets.api.v1.endpoints

import org.ergoplatform.common.http.HttpError
import org.ergoplatform.common.models.TimeWindow
import org.ergoplatform.dex.domain.amm.PoolId
import org.ergoplatform.dex.markets.api.v1.models.amm.{PlatformSummary, PoolSummary}
import sttp.tapir.{path, Endpoint}
import sttp.tapir._
import sttp.tapir.json.circe.jsonBody

final class AmmStatsEndpoints {

  val PathPrefix = "amm"
  val Group      = "ammStats"

  def endpoints: List[Endpoint[_, _, _, _]] = getPlatformStats :: getPoolStats :: Nil

  def getPoolStats: Endpoint[(PoolId, TimeWindow), HttpError, PoolSummary, Any] =
    baseEndpoint.get
      .in(PathPrefix / "pool" / path[PoolId].description("Asset reference") / "stats")
      .in(timeWindow)
      .out(jsonBody[PoolSummary])
      .tag(Group)
      .name("Pool stats")
      .description("Get statistics on a pool with the given ID")

  def getPlatformStats: Endpoint[TimeWindow, HttpError, PlatformSummary, Any] =
    baseEndpoint.get
      .in(PathPrefix / "platform" / "stats")
      .in(timeWindow)
      .out(jsonBody[PlatformSummary])
      .tag(Group)
      .name("Platform stats")
      .description("Get statistics on whole AMM")
}
