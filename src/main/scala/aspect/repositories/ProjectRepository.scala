package aspect.repositories

import akka.pattern.pipe
import aspect.common.Messages.Start
import aspect.common.actors.{BaseActor, NodeSingleton}
import aspect.common.mongo.MongoDatabase
import aspect.domain.{Project, ProjectId, UserId}
import aspect.mongo.{BsonProtocol, ProjectCollection}

object ProjectRepository extends NodeSingleton[ProjectRepository] {
  case class GetUserProjects(userId: UserId)
  case class UserProjects(userId: UserId, projects: List[Project])

  case class FindProjectById(projectId: ProjectId)
  case class ProjectFoundById(project: Project)
  case class ProjectNotFoundById(projectId: ProjectId)

  case class AddProject(project: Project)
  case class ProjectAdded(projectId: ProjectId)

  case class RemoveProject(projectId: ProjectId)
  case class ProjectRemoved(projectId: ProjectId)

  case class UpdateProject(projectId: ProjectId, name: Option[String])
  case class ProjectUpdated(projectId: ProjectId)
}

class ProjectRepository extends BaseActor with BsonProtocol {
  import ProjectRepository._
  import context.dispatcher

  val collection = new ProjectCollection(MongoDatabase.db)

  def receive: Receive = {
    case Start => collection.ensureIndexes

    case GetUserProjects(userId) =>
      collection.getUserProjects(userId) map (UserProjects(userId, _)) pipeTo sender

    case FindProjectById(projectId) =>
      collection.get(projectId) map {
        case Some(project) => ProjectFoundById(project)
        case None => ProjectNotFoundById(projectId)
      } pipeTo sender

    case AddProject(project) =>
      collection.add(project) map (_ => ProjectAdded(project.id)) pipeTo sender

    case RemoveProject(projectId) =>
      collection.remove(projectId) map (_ => ProjectRemoved(projectId)) pipeTo sender

    case UpdateProject(projectId, projectName) =>
      collection.update(projectId, projectName) map (_ => ProjectUpdated(projectId)) pipeTo sender
  }
}
