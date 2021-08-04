package org.ergoplatform.common.streaming

import derevo.derive
import derevo.pureconfig.pureconfigReader
import tofu.Context
import tofu.logging.derivation.loggable

import scala.concurrent.duration.FiniteDuration

@derive(pureconfigReader, loggable)
final case class RotationConfig(retryDelay: FiniteDuration, orderLifetime: FiniteDuration)

object RotationConfig extends Context.Companion[RotationConfig]
