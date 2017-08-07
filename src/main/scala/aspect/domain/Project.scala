package aspect.domain

import java.time.LocalDateTime

import aspect.common.EntityId

case class ProjectId(value: String) extends EntityId

case class Project(id: ProjectId,
                   userId: UserId,
                   name: String,
                   createdAt: LocalDateTime)
