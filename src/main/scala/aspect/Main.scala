package aspect

import akka.actor.ActorSystem
import aspect.common.Messages.Start
import aspect.common.config.{Application, Settings}
import aspect.common.mongo.MongoDatabase
import com.typesafe.config.Config

import scala.io.StdIn._

object Main extends App {
  val system = ActorSystem("aspect", Application.config)
  val settings = MainSettings(Application.config)
  system.actorOf(Guardian.props(settings.aspect), "aspect") ! Start
  while (readLine() != "exit") { }
  MongoDatabase.driver.close()
  system.terminate()
}

case class MainSettings(config: Config) extends Settings {
  val aspect: GuardianSettings = get[GuardianSettings]("aspect")
}
