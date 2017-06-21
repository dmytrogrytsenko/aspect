package aspect.common.settings

import com.typesafe.config.Config

trait SettingsReader[T] {
  def read(config: Config, name: String): T
}
