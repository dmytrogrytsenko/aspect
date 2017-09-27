package aspect.common.config

import com.typesafe.config.Config

object Implicits {
  implicit class ConfigOps(config: Config) {
    def getConfigOpt(path: String): Option[Config] =
      if (config.hasPath(path)) Some(config.getConfig(path)) else None
  }
}
