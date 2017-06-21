package aspect

import akka.actor.Props
import aspect.common.Messages.Start
import aspect.common.actors.BaseActor
import aspect.settings.GuardianSettings

object Guardian {
  def props(settings: GuardianSettings): Props =
    Props(classOf[Guardian], settings)
}

class Guardian(settings: GuardianSettings) extends BaseActor {
  def receive = {
    case Start =>
  }
}
