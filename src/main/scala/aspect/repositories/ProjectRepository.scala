package aspect.repositories

import aspect.common.actors.{BaseActor, NodeSingleton}
import aspect.domain.{Project, ProjectId, UserId}

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

class ProjectRepository extends BaseActor {
  import ProjectRepository._

  def receive: Receive = working(Nil)

  def working(projects: List[Project]): Receive = {
    case GetUserProjects(userId) =>
      sender ! UserProjects(userId, projects.filter(_.userId == userId))

    case FindProjectById(projectId) =>
      val result = projects.find(_.id == projectId) match {
        case Some(project) => ProjectFoundById(project)
        case None => ProjectNotFoundById(projectId)
      }
      sender ! result

    case AddProject(project) =>
      become(working(project +: projects.filterNot(_.id == project.id)))
      sender ! ProjectAdded(project.id)

    case RemoveProject(projectId) =>
      become(working(projects.filterNot(_.id == projectId)))
      sender !  ProjectRemoved(projectId)

    case UpdateProject(projectId, projectName) =>
      projects.find(_.id == projectId).foreach { project =>
        val updatedProject = project.copy(name = projectName.getOrElse(project.name))
        become(working(updatedProject +: projects.filterNot(_.id == projectId)))
      }
      sender ! ProjectUpdated(projectId)
  }
}