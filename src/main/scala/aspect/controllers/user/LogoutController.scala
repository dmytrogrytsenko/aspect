package aspect.controllers.user

import akka.actor.Props
import aspect.common.Messages.{Done, Start}
import aspect.domain.SessionToken
import aspect.repositories.SessionRepository
import aspect.repositories.SessionRepository.{RemoveSession, SessionRemoved}
import aspect.rest.Controller

object LogoutController {
  def props(token: SessionToken) = Props(classOf[LogoutController], token)
}

class LogoutController(token: SessionToken) extends Controller {
  def receive: Receive = {
    case Start => SessionRepository.endpoint ! RemoveSession(token)
    case SessionRemoved(`token`) => complete(Done)
  }
}