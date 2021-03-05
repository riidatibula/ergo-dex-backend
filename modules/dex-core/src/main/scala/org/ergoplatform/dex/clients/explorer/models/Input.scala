package org.ergoplatform.dex.clients.explorer.models

import derevo.circe.decoder
import derevo.derive
import io.circe.Json
import org.ergoplatform.dex.{Address, BoxId, HexString, SErgoTree, TxId}
import tofu.logging.derivation.loggable

@derive(decoder, loggable)
final case class Input(
  id: BoxId,
  value: Long,
  index: Int,
  spendingProof: Option[HexString],
  outputTransactionId: TxId,
  outputIndex: Int,
  ergoTree: SErgoTree,
  address: Address,
  assets: List[Asset],
  additionalRegisters: Json
)
