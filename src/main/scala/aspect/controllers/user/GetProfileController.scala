package aspect.controllers.user

import akka.actor.Props
import aspect.common.Messages.Start
import aspect.domain.UserId
import aspect.repositories.UserRepository
import aspect.repositories.UserRepository.{FindUserById, UserFoundById, UserNotFoundById}
import aspect.rest.Controller
import aspect.rest.RestErrors.Unauthorized
import aspect.rest.models.ProfileResult

object GetProfileController {
  def props(userId: UserId) = Props(classOf[GetProfileController], userId)
}

class GetProfileController(userId: UserId) extends Controller {
  def receive: Receive = {
    case Start => UserRepository.endpoint ! FindUserById(userId)
    case UserFoundById(user) => complete(ProfileResult(user))
    case UserNotFoundById(`userId`) => failure(Unauthorized.credentialsRejected)
  }
}