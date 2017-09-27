package aspect.controllers.project

import akka.actor.Props
import aspect.common.Messages.Start
import aspect.domain.UserId
import aspect.repositories.ProjectRepository.{GetUserProjects, UserProjects}
import aspect.repositories.{ProjectRepository, UserRepository}
import aspect.repositories.UserRepository.{FindUserById, UserFoundById, UserNotFoundById}
import aspect.rest.Controller
import aspect.rest.RestErrors.Unauthorized
import aspect.rest.models.{ProjectItemResult, ProjectListResult}

object GetProjectsController {
  def props(userId: UserId) = Props(classOf[GetProjectsController], userId)
}

class GetProjectsController(userId: UserId) extends Controller {
  def receive: Receive = {
    case Start => UserRepository.endpoint ! FindUserById(userId)
    case UserFoundById(user) => ProjectRepository.endpoint ! GetUserProjects(userId)
    case UserNotFoundById(`userId`) => failure(Unauthorized.credentialsRejected)
    case UserProjects(`userId`, projects) => complete(ProjectListResult(projects.map(ProjectItemResult.apply)))
  }
}