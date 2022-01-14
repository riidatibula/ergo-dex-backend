package org.ergoplatform.dex.index.db

import org.ergoplatform.dex.domain.Ticker
import org.ergoplatform.dex.domain.amm.OrderEvaluation.{DepositEvaluation, RedeemEvaluation, SwapEvaluation}
import org.ergoplatform.dex.domain.amm._
import org.ergoplatform.ergo._

object models {

  final case class DBPool(
    stateId: PoolStateId,
    poolId: PoolId,
    lpId: TokenId,
    lpAmount: Long,
    xId: TokenId,
    xAmount: Long,
    yId: TokenId,
    yAmount: Long,
    feeNum: Int,
    globalIndex: Long,
    protocolVersion: ProtocolVersion
  )

  implicit val poolView: Extract[CFMMPool, DBPool] =
    pool =>
      DBPool(
        PoolStateId.fromBoxId(pool.box.boxId),
        pool.poolId,
        pool.lp.id,
        pool.lp.value,
        pool.x.id,
        pool.x.value,
        pool.y.id,
        pool.y.value,
        pool.feeNum,
        pool.box.lastConfirmedBoxGix,
        ProtocolVersion.Initial
      )

  final case class DBSwap(
    orderId: OrderId,
    poolId: PoolId,
    poolStateId: Option[PoolStateId],
    maxMinerFee: Long,
    timestamp: Long,
    inputId: TokenId,
    inputValue: Long,
    minOutputId: TokenId,
    minOutputAmount: Long,
    outputAmount: Option[Long],
    dexFeePerTokenNum: Long,
    dexFeePerTokenDenom: Long,
    p2pk: Address,
    protocolVersion: ProtocolVersion
  )

  implicit val swapView: Extract[EvaluatedCFMMOrder[Swap, SwapEvaluation], DBSwap] = {
    case EvaluatedCFMMOrder(swap, ev, pool) =>
      DBSwap(
        OrderId.fromBoxId(swap.box.boxId),
        swap.poolId,
        pool.map(p => PoolStateId(p.box.boxId)),
        swap.maxMinerFee,
        swap.timestamp,
        swap.params.input.id,
        swap.params.input.value,
        swap.params.minOutput.id,
        swap.params.minOutput.value,
        ev.map(_.output.value),
        swap.params.dexFeePerTokenNum,
        swap.params.dexFeePerTokenDenom,
        swap.params.p2pk,
        ProtocolVersion.Initial
      )
  }

  final case class DBRedeem(
    orderId: OrderId,
    poolId: PoolId,
    poolStateId: Option[PoolStateId],
    maxMinerFee: Long,
    timestamp: Long,
    lpId: TokenId,
    lpAmount: Long,
    outputAmountX: Option[Long],
    outputAmountY: Option[Long],
    dexFee: Long,
    p2pk: Address,
    protocolVersion: ProtocolVersion
  )

  implicit val redeemView: Extract[EvaluatedCFMMOrder[Redeem, RedeemEvaluation], DBRedeem] = {
    case EvaluatedCFMMOrder(redeem, ev, pool) =>
      DBRedeem(
        OrderId.fromBoxId(redeem.box.boxId),
        redeem.poolId,
        pool.map(p => PoolStateId(p.box.boxId)),
        redeem.maxMinerFee,
        redeem.timestamp,
        redeem.params.lp.id,
        redeem.params.lp.value,
        ev.map(_.outputX.value),
        ev.map(_.outputY.value),
        redeem.params.dexFee,
        redeem.params.p2pk,
        ProtocolVersion.Initial
      )
  }

  final case class DBDeposit(
    orderId: OrderId,
    poolId: PoolId,
    poolStateId: Option[PoolStateId],
    maxMinerFee: Long,
    timestamp: Long,
    inputIdX: TokenId,
    inputAmountX: Long,
    inputIdY: TokenId,
    inputAmountY: Long,
    outputAmountLP: Option[Long],
    dexFee: Long,
    p2pk: Address,
    protocolVersion: ProtocolVersion
  )

  implicit val depositView: Extract[EvaluatedCFMMOrder[Deposit, DepositEvaluation], DBDeposit] = {
    case EvaluatedCFMMOrder(deposit, ev, pool) =>
      DBDeposit(
        OrderId.fromBoxId(deposit.box.boxId),
        deposit.poolId,
        pool.map(p => PoolStateId(p.box.boxId)),
        deposit.maxMinerFee,
        deposit.timestamp,
        deposit.params.inX.id,
        deposit.params.inX.value,
        deposit.params.inY.id,
        deposit.params.inY.value,
        ev.map(_.outputLP.value),
        deposit.params.dexFee,
        deposit.params.p2pk,
        ProtocolVersion.Initial
      )
  }

  final case class DBAssetInfo(
    tokenId: TokenId,
    ticker: Option[Ticker],
    decimals: Option[Int]
  )

  type PoolAssets = (DBAssetInfo, DBAssetInfo, DBAssetInfo)

  implicit val extractAssets: Extract[CFMMPool, PoolAssets] =
    pool =>
      (
        DBAssetInfo(pool.lp.id, pool.lpInfo.ticker, pool.lpInfo.decimals),
        DBAssetInfo(pool.x.id, pool.xInfo.ticker, pool.xInfo.decimals),
        DBAssetInfo(pool.y.id, pool.yInfo.ticker, pool.yInfo.decimals)
      )
}
