package aspect

import akka.actor.Props
import aspect.common.Messages.Start
import aspect.common.actors.{BaseActor, NodeRings, NodeRingsSettings}
import aspect.common.config.Settings
import aspect.rest.{RestEndpoint, RestEndpointSettings}
import com.typesafe.config.Config

object Guardian {
  def props(settings: GuardianSettings): Props =
    Props(classOf[Guardian], settings)
}

class Guardian(settings: GuardianSettings) extends BaseActor {
  def receive: Receive = {
    case Start =>
      NodeRings.create(settings.rings)
      RestEndpoint.create(settings.rest)
  }
}

case class GuardianSettings(config: Config) extends Settings {
  val rings: NodeRingsSettings = get[NodeRingsSettings]("rings")
  val rest: RestEndpointSettings = get[RestEndpointSettings]("rest")
}
