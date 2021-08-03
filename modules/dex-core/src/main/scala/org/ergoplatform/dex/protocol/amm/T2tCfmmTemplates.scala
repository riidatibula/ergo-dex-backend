package org.ergoplatform.dex.protocol.amm

import org.ergoplatform.dex.protocol.amm.AMMType.T2TCFMM
import org.ergoplatform.ergo.ErgoTreeTemplate

object T2tCfmmTemplates extends ContractTemplates[T2TCFMM] {

  def deposit: ErgoTreeTemplate =
    ErgoTreeTemplate.unsafeFromString(
      "d808d6017300d602db6308b2a4730100d603b2a5730200d604b2db63087203730300d605b27202730400d606db6308a7d6077e9973058c" +
      "72050206d6087e8cb272027306000206eb027201d1ed93b27202730700860273087309edededed93c27203d0720192c1720399c1a7730a" +
      "938c7204018c720501927e8c72040206a19d9c7e8cb27206730b000206720772089d9c7e8cb27206730c00020672077208e5dc2407c672" +
      "03040e01d901090e937209c5a7730d"
    )

  def redeem: ErgoTreeTemplate =
    ErgoTreeTemplate.unsafeFromString(
      "d809d6017300d602db6308b2a4730100d603b2a5730200d604db63087203d605b27204730300d606b27202730400d6078c720601d608b2" +
      "7204730500d6099d9c7e8cb2db6308a773060002067e8c720602067e9973078cb272027308000206eb027201d1ed93b272027309008602" +
      "730a730bedededededed93c27203d0720192c1720399c1a7730c938c7205017207938c7208017207927e8c720502067209927e8c720802" +
      "067209e5dc2407c67203040e01d9010a0e93720ac5a7730d"
    )

  def swap: ErgoTreeTemplate =
    ErgoTreeTemplate.unsafeFromString(
      "d806d6017300d602b1a4d603b2a4730100d6047302d6057303d6067304eb027201d195ed947202730593b1db630872037306d80ad607db" +
      "63087203d608b2a5730700d609b2db63087208730800d60a8c720902d60b7e720a06d60cb27207730900d60d7e8c720c0206d60e7e8cb2" +
      "db6308a7730a000206d60f7e8cb27207730b000206d6109a720b730cedededededed938cb27207730d0001730e937202730f93c27208d0" +
      "7201938c720901720492720a7310927ec1720806997ec1a7069d9c720b7e7311067e73120695938c720c017204909c9c720d720e7e7205" +
      "069c72109a9c720f7e7206069c720e7e720506909c9c720f720e7e7205069c72109a9c720d7e7206069c720e7e7205067313"
    )
}
