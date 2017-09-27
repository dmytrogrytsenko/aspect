package aspect.controllers.user

import akka.actor.Props
import aspect.common.Messages.Start
import aspect.domain.Session
import aspect.repositories.SessionRepository.{AddSession, SessionAdded}
import aspect.repositories.{SessionRepository, UserRepository}
import aspect.repositories.UserRepository.{FindUserByName, UserFoundByName, UserNotFoundByName}
import aspect.rest.Controller
import aspect.rest.RestErrors.Unauthorized
import aspect.rest.models.{LoginData, LoginResult}

object LoginController {
  def props(data: LoginData): Props =
    Props(classOf[LoginController], data)
}

class LoginController(data: LoginData) extends Controller {
  def receive: Receive = {
    case Start => UserRepository.endpoint ! FindUserByName(data.login)
    case UserFoundByName(user) if user.password == data.password =>
      SessionRepository.endpoint ! AddSession(Session.create(user.id))
    case _: UserFoundByName => failure(Unauthorized.credentialsRejected)
    case _: UserNotFoundByName => failure(Unauthorized.credentialsRejected)
    case SessionAdded(token) => complete(LoginResult(token))
  }
}
