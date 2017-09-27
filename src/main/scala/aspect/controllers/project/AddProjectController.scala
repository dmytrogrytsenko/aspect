package aspect.controllers.project

import akka.actor.Props
import aspect.common.Messages.Start
import aspect.domain.{Project, UserId}
import aspect.repositories.ProjectRepository.{AddProject, ProjectAdded}
import aspect.repositories.{ProjectRepository, UserRepository}
import aspect.repositories.UserRepository.{FindUserById, UserFoundById, UserNotFoundById}
import aspect.rest.Controller
import aspect.rest.RestErrors.Unauthorized
import aspect.rest.models.{AddProjectData, AddProjectResult}

object AddProjectController {
  def props(userId: UserId, data: AddProjectData) =
    Props(classOf[AddProjectController], userId, data)
}

class AddProjectController(userId: UserId, data: AddProjectData) extends Controller {
  def receive: Receive = {
    case Start =>
      UserRepository.endpoint ! FindUserById(userId)
    case _: UserFoundById =>
      ProjectRepository.endpoint ! AddProject(Project.create(userId, data.name))
    case UserNotFoundById(`userId`) =>
      failure(Unauthorized.credentialsRejected)
    case ProjectAdded(projectId) =>
      complete(AddProjectResult(projectId))
  }
}
