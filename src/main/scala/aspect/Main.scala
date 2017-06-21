package aspect

import akka.actor.ActorSystem
import aspect.common.Messages.Start
import aspect.settings.MainSettings
import com.typesafe.config.ConfigFactory

import scala.io.StdIn._

object Main extends App {
  val config = ConfigFactory.load()
  val system = ActorSystem("aspect", config)
  val settings = MainSettings(config)
  system.actorOf(Guardian.props(settings.aspect), "aspect") ! Start
  while (readLine() != "exit") { }
  system.terminate()
}
