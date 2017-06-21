package aspect.settings

import aspect.common.settings.Settings
import com.typesafe.config.Config

case class MainSettings(config: Config) extends Settings {
  val aspect = get[GuardianSettings]("aspect")
}
