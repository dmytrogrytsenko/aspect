package aspect.controllers.project

import akka.actor.Props
import aspect.common.Messages.{Done, Start}
import aspect.domain.{Project, ProjectId, User, UserId}
import aspect.repositories.ProjectRepository._
import aspect.repositories.{ProjectRepository, UserRepository}
import aspect.repositories.UserRepository.{FindUserById, UserFoundById, UserNotFoundById}
import aspect.rest.Controller
import aspect.rest.RestErrors.{Forbidden, NotFound, Unauthorized}

object RemoveProjectController {
  def props(userId: UserId, projectId: ProjectId) =
    Props(classOf[RemoveProjectController], userId, projectId)
}

class RemoveProjectController(userId: UserId, projectId: ProjectId) extends Controller {
  private var user: User = _
  private var project: Project = _

  def receive: Receive = {
    case Start =>
      UserRepository.endpoint ! FindUserById(userId)
      ProjectRepository.endpoint ! FindProjectById(projectId)
    case UserFoundById(receivedUser) => user = receivedUser; checkCollected()
    case ProjectFoundById(receivedProject) => project = receivedProject; checkCollected()
    case UserNotFoundById(`userId`) => failure(Unauthorized.credentialsRejected)
    case ProjectNotFoundById(`projectId`) => failure(NotFound.projectNotFound)
    case ProjectRemoved(`projectId`) => complete(Done)
  }

  def checkCollected(): Unit =
    if (user != null && project != null) {
      if (project.userId != user.id) failure(Forbidden.accessDenied)
      else ProjectRepository.endpoint ! RemoveProject(projectId)
    }
}