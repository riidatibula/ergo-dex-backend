package org.ergoplatform.dex.demo

import org.ergoplatform.ErgoBox._
import org.ergoplatform._
import org.ergoplatform.dex.protocol.codecs._
import org.ergoplatform.dex.protocol.sigmaUtils
import org.ergoplatform.dex.sources.{n2tContracts, t2tContracts}
import org.ergoplatform.ergo.syntax._
import org.ergoplatform.wallet.interpreter.ErgoUnsafeProver
import scorex.crypto.authds.ADKey
import scorex.crypto.hash.Digest32
import scorex.util.encode.Base16
import sigmastate.SType
import sigmastate.Values.{ErgoTree, EvaluatedValue, IntConstant}
import sigmastate.eval._
import sigmastate.lang.Terms.ValueOps

object CreateCfmmPool extends App with SigmaPlatform {

  val secretHex = ""
  val inputId   = "1c51c3a53abfe87e6db9a03c649e8360f255ffc4bd34303d30fc7db23ae551db"

  val input = getInput(inputId).get

  val lpId       = Digest32 @@ (ADKey !@@ input.boxId.toErgo)
  val burnLP     = 1000
  val emissionLP = Long.MaxValue - burnLP

  val lpName = "WERG_WADA_LP_2"

  val lockNErgs = 4000000L
  val minValue  = 500000L

  val bootInputNErg = minerFeeNErg + lockNErgs + minValue

  require(input.value >= bootInputNErg + minerFeeNErg)

  val depositWERG = 5000000000000L
  val depositWADA = 10000000000000L

  val WERG = getToken("ef802b475c06189fdbf844153cdc1d449a5ba87cce13d11bb47b5a539f27f12b", input)
  val WADA = getToken("30974274078845f263b4f21787e33cc99e9ec19a17ad85a5bc6da2cca91c5a2e", input)

  val TERG = getToken("f45c4f0d95ce1c64defa607d94717a9a30c00fdd44827504be20db19f4dce36f", input)
  val TUSD = getToken("f302c6c042ada3566df8e67069f8ac76d10ce15889535141593c168f3c59e776", input)

  require(WERG._2 >= depositWERG)
  require(WADA._2 >= depositWADA)

  val height = currentHeight()

  // Init TX

  val boot = new ErgoBoxCandidate(
    value            = bootInputNErg,
    ergoTree         = selfAddress.script,
    creationHeight   = height,
    additionalTokens = Colls.fromItems(lpId -> emissionLP, WERG._1 -> depositWERG, WADA._1 -> depositWADA)
  )

  val change0 = new ErgoBoxCandidate(
    value          = input.value - bootInputNErg - minerFeeNErg,
    ergoTree       = selfAddress.script,
    creationHeight = height,
    additionalTokens =
      Colls.fromItems(WERG._1 -> (WERG._2 - depositWERG), WADA._1 -> (WADA._2 - depositWADA), TERG, TUSD)
  )

  val inputs0 = Vector(new UnsignedInput(ADKey @@ Base16.decode(inputId).get))
  val outs0   = Vector(boot, change0, minerFeeBox)
  val utx0    = UnsignedErgoLikeTransaction(inputs0, outs0)
  val tx0     = ErgoUnsafeProver.prove(utx0, sk)

  // Pool TX

  val shareLP = math.sqrt((depositWERG * depositWADA).toDouble).toLong

  require(shareLP * shareLP <= depositWERG * depositWADA)

  val lpOut = new ErgoBoxCandidate(
    value            = minValue,
    ergoTree         = selfAddress.script,
    creationHeight   = height,
    additionalTokens = Colls.fromItems(lpId -> shareLP)
  )

  val bootBox = tx0.outputs(0)

  val poolFeeNum = 996
  val poolNFT    = Digest32 @@ (ADKey !@@ bootBox.id)

  val poolRegisters: Map[NonMandatoryRegisterId, _ <: EvaluatedValue[_ <: SType]] =
    scala.Predef.Map(
      R4 -> IntConstant(poolFeeNum)
    )

  val poolTree =
    sigmaUtils.updateVersionHeader(ErgoTree.fromProposition(sigma.compile(Map.empty, t2tContracts.pool).asSigmaProp))

  val poolOut = new ErgoBoxCandidate(
    value          = lockNErgs,
    ergoTree       = poolTree,
    creationHeight = height,
    additionalTokens = Colls.fromItems(
      poolNFT -> 1L,
      lpId    -> (emissionLP - shareLP),
      WERG._1 -> depositWERG,
      WADA._1 -> depositWADA
    ),
    additionalRegisters = poolRegisters
  )

  val inputs1 = Vector(new UnsignedInput(bootBox.id))
  val outs1   = Vector(poolOut, lpOut, minerFeeBox)
  val utx1    = UnsignedErgoLikeTransaction(inputs1, outs1)
  val tx1     = ErgoUnsafeProver.prove(utx1, sk)

  println("Submitting init tx")
  val s0 = submitTx(tx0)
  println(s0.map(id => s"Done. $id"))
  println("Submitting pool tx")
  val s1 = submitTx(tx1)
  println(s1.map(id => s"Done. $id"))
}

object CreateNativeCfmmPool extends App with SigmaPlatform {

  val secretHex = ""

  val inputsIds =
    "1f2d9bfbf99f6ef9b259115dfe6bc83252855e76f9b43896f53e1dc190cfa8da" ::
    "93e017eb7e3e97d0989ff08892aa80ab91e7204a58f1a90fe23c01db66481dc7" :: Nil

  val inputs       = inputsIds.map(inputId => getInput(inputId).get)
  val totalNErgsIn = inputs.map(_.value).sum
  val tokensIn     = extractTokens(inputs)

  val lpId       = Digest32 @@ (ADKey !@@ inputs(0).boxId.toErgo)
  val burnLP     = 1000
  val emissionLP = Long.MaxValue - burnLP

  val lpName = "ERG_SigUSD_LP"

  val lockNErgs = 10000000L
  val minValue  = 500000L

  val depositNErgs       = 1L * math.pow(10, 9).toLong
  val depositSigUSDCents = 2000L

  val bootInputNErg = minerFeeNErg + lockNErgs + minValue + depositNErgs

  val SigUSD = getToken("03faf2cb329f2e90d6d23b58d91bbb6c046aa143261cc21f52fbe2824bfcbf04", inputs)

  val height = currentHeight()

  // Init TX

  val boot = new ErgoBoxCandidate(
    value            = bootInputNErg,
    ergoTree         = selfAddress.script,
    creationHeight   = height,
    additionalTokens = Colls.fromItems(lpId -> emissionLP, SigUSD._1 -> depositSigUSDCents)
  )

  val change0 = new ErgoBoxCandidate(
    value          = totalNErgsIn - bootInputNErg - minerFeeNErg,
    ergoTree       = selfAddress.script,
    creationHeight = height,
    additionalTokens = Colls.fromItems(tokensIn.filterNot { case (tid, _) =>
      java.util.Arrays.equals(tid, SigUSD._1)
    }: _*)
  )

  val inputs0 = inputs.map(input => new UnsignedInput(ADKey @@ Base16.decode(input.boxId.value).get)).toVector
  val outs0   = Vector(boot, change0, minerFeeBox)
  val utx0    = UnsignedErgoLikeTransaction(inputs0, outs0)
  val tx0     = ErgoUnsafeProver.prove(utx0, sk)

  // Pool TX

  val shareLP = math.sqrt(((depositNErgs + lockNErgs) * depositSigUSDCents).toDouble).toLong

  require(shareLP * shareLP <= (depositNErgs + lockNErgs) * depositSigUSDCents)

  val lpOut = new ErgoBoxCandidate(
    value            = minValue,
    ergoTree         = selfAddress.script,
    creationHeight   = height,
    additionalTokens = Colls.fromItems(lpId -> shareLP)
  )

  val bootBox = tx0.outputs(0)

  val poolFeeNum = 995
  val poolNFT    = Digest32 @@ (ADKey !@@ bootBox.id)

  val poolRegisters: Map[NonMandatoryRegisterId, _ <: EvaluatedValue[_ <: SType]] =
    scala.Predef.Map(
      R4 -> IntConstant(poolFeeNum)
    )

  val poolTree =
    sigmaUtils.updateVersionHeader(ErgoTree.fromProposition(sigma.compile(Map.empty, n2tContracts.pool).asSigmaProp))

  val poolOut = new ErgoBoxCandidate(
    value          = depositNErgs + lockNErgs,
    ergoTree       = poolTree,
    creationHeight = height,
    additionalTokens = Colls.fromItems(
      poolNFT   -> 1L,
      lpId      -> (emissionLP - shareLP),
      SigUSD._1 -> depositSigUSDCents
    ),
    additionalRegisters = poolRegisters
  )

  val inputs1 = Vector(new UnsignedInput(bootBox.id))
  val outs1   = Vector(poolOut, lpOut, minerFeeBox)
  val utx1    = UnsignedErgoLikeTransaction(inputs1, outs1)
  val tx1     = ErgoUnsafeProver.prove(utx1, sk)

  println("Submitting init tx")
  val s0 = submitTx(tx0)
  println(s0.map(id => s"Done. $id"))
  println("Submitting pool tx")
  val s1 = submitTx(tx1)
  println(s1.map(id => s"Done. $id"))
}
