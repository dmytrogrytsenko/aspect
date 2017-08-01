package aspect

import akka.actor.ActorSystem
import aspect.common.Messages.Start
import aspect.common.config.{Application, Settings}
import com.typesafe.config.Config

import scala.io.StdIn._

object Main extends App {
  val system = ActorSystem("aspect", Application.config)
  val settings = MainSettings(Application.config)
  system.actorOf(Guardian.props(settings.aspect), "aspect") ! Start
  while (readLine() != "exit") { }
  system.terminate()
}

case class MainSettings(config: Config) extends Settings {
  val aspect: GuardianSettings = get[GuardianSettings]("aspect")
}
