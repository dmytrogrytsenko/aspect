package aspect.domain

import java.time.LocalDateTime

import aspect.common.{EntityId, EntityIdCompanion, utc, uuid}

case class ProjectId(value: String) extends EntityId

object ProjectId extends EntityIdCompanion[ProjectId]

case class Project(id: ProjectId,
                   userId: UserId,
                   name: String,
                   createdAt: LocalDateTime)

object Project {
  def create(userId: UserId, name: String): Project =
    Project(ProjectId(uuid.toString), userId, name, utc)
}
