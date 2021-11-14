package org.ergoplatform.dex.protocol.amm

import org.ergoplatform.ergo.ErgoTreeTemplate

object N2TCFMMTemplates {

  def deposit: ErgoTreeTemplate =
    ErgoTreeTemplate.unsafeFromString(
      "d803d6017300d602b2a4730100d6037302eb027201d195ed93b1a4730393b1db630872027304d80bd604db63087202d605b2a5730500d6" +
      "06b27204730600d6077e9973078c72060206d6087ec1720206d6099d9c7e72030672077208d60ab27204730800d60b7e8c720a0206d60c" +
      "9d9c7e8cb2db6308a773090002067207720bd60ddb63087205d60eb2720d730a00edededed938cb27204730b0001730c93c27205d07201" +
      "95ed8f7209720c93b1720d730dd801d60fb2720d730e00eded92c172059999c1a7730f7310938c720f018c720a01927e8c720f02069d9c" +
      "99720c7209720b720795927209720c927ec1720506997e99c1a7731106997e7203069d9c997209720c720872077312938c720e018c7206" +
      "01927e8c720e0206a17209720c7313"
    )

  def redeem: ErgoTreeTemplate =
    ErgoTreeTemplate.unsafeFromString(
      "d802d6017300d602b2a4730100eb027201d195ed93b1a4730293b1db630872027303d806d603db63087202d604b2a5730400d605b2db63" +
      "087204730500d606b27203730600d6077e8cb2db6308a77307000206d6087e9973088cb272037309000206edededed938cb27203730a00" +
      "01730b93c27204d07201938c7205018c720601927e9a99c17204c1a7730c069d9c72077ec17202067208927e8c720502069d9c72077e8c" +
      "720602067208730d"
    )

  def swapSell: ErgoTreeTemplate =
    ErgoTreeTemplate.unsafeFromString(
      "d803d6017300d602b2a4730100d6037302eb027201d195ed93b1a4730393b1db630872027304d804d604db63087202d605b2a5730500d6" +
      "06b2db63087205730600d6077e8c72060206ededededed938cb2720473070001730893c27205d07201938c72060173099272077e730a06" +
      "927ec172050699997ec1a7069d9c72077e730b067e730c067e720306909c9c7e8cb27204730d0002067e7203067e730e069c9a7207730f" +
      "9a9c7ec17202067e7310067e9c73117e731205067313"
    )

  def swapBuy: ErgoTreeTemplate =
    ErgoTreeTemplate.unsafeFromString(
      "d802d6017300d602b2a4730100eb027201d195ed93b1a4730293b1db630872027303d804d603db63087202d604b2a5730400d6059d9c7e" +
      "99c17204c1a7067e7305067e730606d6068cb2db6308a773070002ededed938cb2720373080001730993c27204d072019272057e730a06" +
      "909c9c7ec17202067e7206067e730b069c9a7205730c9a9c7e8cb27203730d0002067e730e067e9c72067e730f05067310"
    )
}
