package aspect.controllers.target

import akka.actor.Props
import aspect.common.Messages.{Done, Start}
import aspect.domain.{Target, TargetId, User, UserId}
import aspect.repositories.ProjectRepository.{FindProjectById, ProjectFoundById, ProjectNotFoundById}
import aspect.repositories.TargetRepository._
import aspect.repositories.{ProjectRepository, TargetRepository, UserRepository}
import aspect.repositories.UserRepository.{FindUserById, UserFoundById, UserNotFoundById}
import aspect.rest.Controller
import aspect.rest.RestErrors.{Forbidden, NotFound, Unauthorized}
import aspect.rest.models.UpdateTargetData

object UpdateTargetController {
  def props(userId: UserId, targetId: TargetId, data: UpdateTargetData) =
    Props(classOf[UpdateTargetController], userId, targetId, data)
}

class UpdateTargetController(userId: UserId, targetId: TargetId, data: UpdateTargetData) extends Controller {
  def receive: Receive = {
    case Start =>
      UserRepository.endpoint ! FindUserById(userId)
      become(waitingForUser)
  }

  def waitingForUser: Receive = {
    case UserFoundById(user) if user.id == userId =>
      TargetRepository.endpoint ! FindTargetById(targetId)
      become(waitingForTarget(user))
    case UserNotFoundById(`userId`) =>
      failure(Unauthorized.credentialsRejected)
  }

  def waitingForTarget(user: User): Receive = {
    case TargetFoundById(target) if target.id == targetId =>
      ProjectRepository.endpoint ! FindProjectById(target.projectId)
      become(waitingForProject(user, target))
    case TargetNotFoundById(`targetId`) =>
      failure(NotFound.targetNotFound)
  }

  def waitingForProject(user: User, target: Target): Receive = {
    case ProjectFoundById(project) if project.id == target.projectId =>
      if (project.userId == user.id) {
        TargetRepository.endpoint ! UpdateTarget(targetId, data.name, data.keywords)
        become(waitingForTargetUpdated)
      } else {
        failure(Forbidden.accessDenied)
      }
    case ProjectNotFoundById(projectId) if projectId == target.projectId =>
      failure(NotFound.projectNotFound)
  }

  def waitingForTargetUpdated: Receive = {
    case TargetUpdated(`targetId`) => complete(Done)
  }
}
