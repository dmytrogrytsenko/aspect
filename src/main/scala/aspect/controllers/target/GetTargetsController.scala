package aspect.controllers.target

import akka.actor.Props
import aspect.common.Messages.Start
import aspect.domain.{Project, ProjectId, User, UserId}
import aspect.repositories.ProjectRepository.{FindProjectById, ProjectFoundById, ProjectNotFoundById}
import aspect.repositories.TargetRepository.{GetProjectTargets, ProjectTargets}
import aspect.repositories.{ProjectRepository, TargetRepository, UserRepository}
import aspect.repositories.UserRepository.{FindUserById, UserFoundById, UserNotFoundById}
import aspect.rest.Controller
import aspect.rest.RestErrors.{Forbidden, NotFound, Unauthorized}
import aspect.rest.models.{TargetItemResult, TargetListResult}

object GetTargetsController {
  def props(userId: UserId, projectId: ProjectId) =
    Props(classOf[GetTargetsController], userId, projectId)
}

class GetTargetsController(userId: UserId, projectId: ProjectId) extends Controller {

  var user: User = _
  var project: Project = _

  def receive: Receive = {
    case Start =>
      UserRepository.endpoint ! FindUserById(userId)
      ProjectRepository.endpoint ! FindProjectById(projectId)

    case UserFoundById(receivedUser) => user = receivedUser; checkCollected()
    case UserNotFoundById(`userId`) => failure(Unauthorized.credentialsRejected)

    case ProjectFoundById(receivedProject) => project = receivedProject; checkCollected()
    case ProjectNotFoundById(`projectId`) => failure(NotFound.projectNotFound)

    case ProjectTargets(`projectId`, targets) => complete(TargetListResult(targets.map(TargetItemResult.apply)))
  }

  def checkCollected(): Unit =
    if (user != null && project != null) {
      if (project.userId != user.id) failure(Forbidden.accessDenied)
      else TargetRepository.endpoint ! GetProjectTargets(projectId)
    }
}

