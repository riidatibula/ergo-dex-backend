package org.ergoplatform.dex.protocol.amm

import org.ergoplatform.dex.ErgoTreeTemplate
import org.ergoplatform.dex.protocol.amm.AmmContractType.T2tCfmm

object T2tCfmmTemplates extends ContractTemplates[T2tCfmm] {

  def deposit: ErgoTreeTemplate =
    ErgoTreeTemplate.unsafeFromString(
      "d808d6017300d602db6308b2a4730100d603b2a5730200d604b2db63087203730300d605b27202730400d606db6308a7d6077e9973058c" +
      "72050206d6087e8cb272027306000206eb027201d1ed93b27202730700860273087309ededed93c27203d0720192c1720399c1a7730a93" +
      "8c7204018c720501927e8c72040206a19d9c7e8cb27206730b000206720772089d9c7e8cb27206730c00020672077208"
    )

  def redeem: ErgoTreeTemplate =
    ErgoTreeTemplate.unsafeFromString(
      "d809d6017300d602db6308b2a4730100d603b2a5730200d604db63087203d605b27204730300d606b27202730400d6078c720601d608b2" +
      "7204730500d6099d9c7e8cb2db6308a773060002067e8c720602067e9973078cb272027308000206eb027201d1ed93b272027309008602" +
      "730a730bededededed93c27203d0720192c1720399c1a7730c938c7205017207938c7208017207927e8c720502067209927e8c72080206" +
      "7209"
    )

  def swap: ErgoTreeTemplate =
    ErgoTreeTemplate.unsafeFromString(
      "d810d6017300d602b2a4730100d6037302d604db63087202d605b27204730300d6068c720501d607b27204730400d6088c720701d609b2" +
      "db6308a7730500d60a8c720901d60b7e8c72050206d60c998c7209027306d60d7e720c06d60e7e8c72070206d60f7307d6107e9c720c73" +
      "0806eb027201d1ededed93cbc272027203ec93720672039372087203ec937206720a937208720aaea5d9011163d802d613b2db63087211" +
      "730900d6148c721302edededed93c27211d07201938c7213017203927214730a92c1721199c1a79c7214730b959372067203909c9c720b" +
      "720d730c9c7e7214069a9c720e7e720f067210909c9c720e720d730d9c7e7214069a9c720b7e720f067210"
    )
}
