package aspect.domain

import aspect.common.{EntityId, EntityIdCompanion}

trait ProjectId extends EntityId
object ProjectId extends EntityIdCompanion[ProjectId]

case class Project(id: ProjectId, userId: UserId, name: String)
