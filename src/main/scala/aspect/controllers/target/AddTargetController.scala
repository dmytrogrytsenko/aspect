package aspect.controllers.target

import akka.actor.Props
import aspect.common.Messages.Start
import aspect.domain._
import aspect.repositories.ProjectRepository.{FindProjectById, ProjectFoundById, ProjectNotFoundById}
import aspect.repositories.TargetRepository.{AddTarget, TargetAdded}
import aspect.repositories.{ProjectRepository, TargetRepository, UserRepository}
import aspect.repositories.UserRepository.{FindUserById, UserFoundById, UserNotFoundById}
import aspect.rest.Controller
import aspect.rest.RestErrors.{Forbidden, NotFound, Unauthorized}
import aspect.rest.models.{AddTargetData, AddTargetResult}

object AddTargetController {
  def props(userId: UserId, data: AddTargetData) =
    Props(classOf[AddTargetController], userId, data)
}

class AddTargetController(userId: UserId, data: AddTargetData) extends Controller {
  def receive: Receive = {
    case Start =>
      UserRepository.endpoint ! FindUserById(userId)
      become(waitingForUser)
  }

  def waitingForUser: Receive = {
    case UserFoundById(user) =>
      ProjectRepository.endpoint ! FindProjectById(data.projectId)
      become(waitingForProject(user))
    case UserNotFoundById(`userId`) =>
      failure(Unauthorized.credentialsRejected)
  }

  def waitingForProject(user: User): Receive = {
    case ProjectFoundById(project) if project.id == data.projectId =>
      if (project.userId == user.id) {
        val target = Target.create(data.projectId, data.name, data.keywords)
        TargetRepository.endpoint ! AddTarget(target)
        become(waitingForTargetAdded(user, project, target))
      } else {
        failure(Forbidden.accessDenied)
      }
    case ProjectNotFoundById(projectId) if projectId == data.projectId =>
      failure(NotFound.projectNotFound)
  }

  def waitingForTargetAdded(user: User, project: Project, target: Target): Receive = {
    case TargetAdded(targetId) if targetId == target.id =>
      complete(AddTargetResult(targetId))
  }
}