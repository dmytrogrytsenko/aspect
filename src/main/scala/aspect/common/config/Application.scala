package aspect.common.config

import com.typesafe.config.{Config, ConfigFactory}

object Application {
  val config: Config = ConfigFactory.load()
}
