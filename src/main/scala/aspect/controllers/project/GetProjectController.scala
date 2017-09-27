package aspect.controllers.project

import akka.actor.Props
import aspect.common.Messages.Start
import aspect.domain.{Project, ProjectId, User, UserId}
import aspect.repositories.ProjectRepository.{FindProjectById, ProjectFoundById, ProjectNotFoundById}
import aspect.repositories.{ProjectRepository, UserRepository}
import aspect.repositories.UserRepository.{FindUserById, UserFoundById, UserNotFoundById}
import aspect.rest.Controller
import aspect.rest.RestErrors.{Forbidden, NotFound, Unauthorized}
import aspect.rest.models.ProjectResult

object GetProjectController {
  def props(userId: UserId, projectId: ProjectId) =
    Props(classOf[GetProjectController], userId, projectId)
}

class GetProjectController(userId: UserId, projectId: ProjectId) extends Controller {
  private var user: User = _
  private var project: Project = _

  def receive: Receive = {
    case Start =>
      UserRepository.endpoint ! FindUserById(userId)
      ProjectRepository.endpoint ! FindProjectById(projectId)
    case UserFoundById(receivedUser) => user = receivedUser; checkCompleted()
    case ProjectFoundById(receivedProject) => project = receivedProject; checkCompleted()
    case UserNotFoundById(`userId`) => failure(Unauthorized.credentialsRejected)
    case ProjectNotFoundById(`projectId`) => failure(NotFound.projectNotFound)
  }

  def checkCompleted(): Unit =
    if (user != null && project != null) {
      if (project.userId != user.id) failure(Forbidden.accessDenied)
      else complete(ProjectResult(project, user))
    }
}
