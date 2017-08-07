package aspect.controllers.user

import akka.actor.Props
import aspect.common.Messages.Start
import aspect.domain.Session
import aspect.repositories.SessionRepository.{AddSession, SessionAdded}
import aspect.repositories.{SessionRepository, UserRepository}
import aspect.repositories.UserRepository.{FindUserByName, UserFoundByName, UserNotFoundByName}
import aspect.rest.Controller
import aspect.rest.models.{LoginData, LoginResult}

object LoginController {
  def props(data: LoginData): Props = Props(classOf[LoginController], data)
}

class LoginController(data: LoginData) extends Controller {
  def receive: Receive = {
    case Start => UserRepository.endpoint ! FindUserByName(data.login)
    case UserFoundByName(user) if user.passwordHash == data.passwordHash =>
      SessionRepository.endpoint ! AddSession(Session.create(user.id))
    case UserFoundByName(user) => failure(Unauthorized.credentialsRejected)
    case UserNotFoundByName(userName) => failure(Unauthorized.credentialsRejected)
    case SessionAdded(token) => complete(LoginResult(token))
  }
}
