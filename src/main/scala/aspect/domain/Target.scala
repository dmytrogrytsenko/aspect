package aspect.domain

import java.time.LocalDateTime

import aspect.common.{EntityId, EntityIdCompanion, utc, uuid}

case class TargetId(value: String) extends EntityId

object TargetId extends EntityIdCompanion[TargetId]

case class Target(id: TargetId,
                  projectId: ProjectId,
                  name: String,
                  keywords: String,
                  createdAt: LocalDateTime)

object Target {
  def create(projectId: ProjectId, name: String, keywords: String): Target =
    Target(TargetId(uuid.toString), projectId, name, keywords, utc)
}