package aspect.controllers.target

import akka.actor.Props
import aspect.common.Messages.Start
import aspect.domain.{Target, TargetId, User, UserId}
import aspect.repositories.ProjectRepository.{FindProjectById, ProjectFoundById, ProjectNotFoundById}
import aspect.repositories.TargetRepository.{FindTargetById, TargetFoundById, TargetNotFoundById}
import aspect.repositories.{ProjectRepository, TargetRepository, UserRepository}
import aspect.repositories.UserRepository.{FindUserById, UserFoundById, UserNotFoundById}
import aspect.rest.Controller
import aspect.rest.RestErrors.{Forbidden, NotFound, Unauthorized}
import aspect.rest.models.{TargetProjectResult, TargetResult, TargetUserResult}

object GetTargetController {
  def props(userId: UserId, targetId: TargetId) =
    Props(classOf[GetTargetController], userId, targetId)
}

class GetTargetController(userId: UserId, targetId: TargetId) extends Controller {

  def receive: Receive = {
    case Start =>
      UserRepository.endpoint ! FindUserById(userId)
      become(waitingForUser)
  }

  def waitingForUser: Receive = {
    case UserFoundById(user) =>
      TargetRepository.endpoint ! FindTargetById(targetId)
      become(waitingForTarget(user))
    case UserNotFoundById(`userId`) =>
      failure(Unauthorized.credentialsRejected)
  }

  def waitingForTarget(user: User): Receive = {
    case TargetFoundById(target) =>
      ProjectRepository.endpoint ! FindProjectById(target.projectId)
      become(waitingForProject(user, target))
    case TargetNotFoundById(`targetId`) =>
      failure(NotFound.targetNotFound)
  }

  def waitingForProject(user: User, target: Target): Receive = {
    case ProjectFoundById(project) if project.userId == user.id =>
      complete(TargetResult(target, project, user))
    case ProjectFoundById(project) =>
      failure(Forbidden.accessDenied)
    case ProjectNotFoundById(projectId) if projectId == target.projectId =>
      failure(NotFound.projectNotFound)
  }
}