package aspect.rest.models

import java.time.LocalDateTime

import aspect.common.Crypt.Sha256
import aspect.domain._

case class LoginData(login: String, passwordHash: Sha256)
case class LoginResult(token: SessionToken)

case class ProfileResult(id: UserId,
                         name: String,
                         email: String,
                         firstName: Option[String],
                         lastName: Option[String],
                         createdAt: LocalDateTime)

object ProfileResult {
  def apply(user: User): ProfileResult =
    ProfileResult(
      id = user.id,
      name = user.name,
      email = user.email,
      firstName = user.firstName,
      lastName = user.lastName,
      createdAt = user.createdAt)
}

case class ProjectItemResult(id: ProjectId, name: String, createdAt: LocalDateTime)

object ProjectItemResult {
  def apply(project: Project): ProjectItemResult =
    ProjectItemResult(
      id = project.id,
      name = project.name,
      createdAt = project.createdAt)
}

case class ProjectListResult(projects: List[ProjectItemResult])

case class ProjectUserResult(id: UserId, name: String)

object ProjectUserResult {
  def apply(user: User): ProjectUserResult =
    ProjectUserResult(user.id, user.name)
}

case class ProjectResult(id: ProjectId,
                         name: String,
                         owner: ProjectUserResult,
                         createdAt: LocalDateTime)

object ProjectResult {
  def apply(project: Project, user: User): ProjectResult =
    ProjectResult(
      id = project.id,
      name = project.name,
      owner = ProjectUserResult(user),
      createdAt = project.createdAt)
}

case class AddProjectData(name: String)
case class AddProjectResult(projectId: ProjectId)
case class UpdateProjectData(name: Option[String] = None)

case class TargetItemResult(id: TargetId, name: String, keywords: String)

object TargetItemResult {
  def apply(target: Target): TargetItemResult =
    TargetItemResult(target.id, target.name, target.keywords)
}

case class TargetListResult(targets: List[TargetItemResult])

case class TargetUserResult(id: UserId, username: String)

object TargetUserResult {
  def apply(user: User): TargetUserResult =
    TargetUserResult(user.id, user.name)
}

case class TargetProjectResult(id: ProjectId, name: String, owner: TargetUserResult)

object TargetProjectResult {
  def apply(project: Project, user: User): TargetProjectResult =
    TargetProjectResult(
      id = project.id,
      name = project.name,
      owner = TargetUserResult(user))
}

case class TargetResult(id: TargetId,
                        name: String,
                        keywords: String,
                        project: TargetProjectResult,
                        createdAt: LocalDateTime)

object TargetResult {
  def apply(target: Target, project: Project, user: User): TargetResult =
    TargetResult(
      id = target.id,
      name = target.name,
      keywords = target.keywords,
      project = TargetProjectResult(project, user),
      createdAt = target.createdAt)
}

case class AddTargetData(projectId: ProjectId, name: String, keywords: String)
case class AddTargetResult(targetId: TargetId)
case class UpdateTargetData(name: Option[String] = None, keywords: Option[String] = None)
