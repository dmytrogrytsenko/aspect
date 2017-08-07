package aspect.rest.models

import java.time.LocalDateTime

import aspect.domain.{ProjectId, TargetId, UserId}

case class LoginData(login: String, password: String)
case class LoginResult(token: String)

case class ProfileResult(id: UserId,
                         name: String,
                         email: String,
                         firstName: Option[String],
                         lastName: Option[String],
                         createdAt: LocalDateTime)

case class ProjectItemResult(id: ProjectId, name: String, createdAt: LocalDateTime)
case class ProjectListResult(projects: List[ProjectItemResult])

case class ProjectUserResult(id: UserId, name: String)
case class ProjectResult(id: ProjectId, name: String, owner: ProjectUserResult, createdAt: LocalDateTime)

case class AddProjectData(name: String)
case class AddProjectResult(projectId: ProjectId)
case class UpdateProjectData(name: Option[String] = None)

case class TargetItemResult(id: TargetId, name: String, keywords: String)
case class TargetListResult(targets: List[TargetItemResult])

case class TargetUserResult(id: UserId, username: String)
case class TargetProjectResult(id: ProjectId, name: String, owner: TargetUserResult)
case class TargetResult(id: TargetId, name: String, keywords: String, project: TargetProjectResult)

case class AddTargetData(projectId: ProjectId, name: String, keywords: String)
case class AddTargetResult(targetId: TargetId)
case class UpdateTargetData(name: Option[String] = None, keywords: Option[String] = None)
