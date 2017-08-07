package aspect.domain

import aspect.common.EntityId

case class TargetId(value: String) extends EntityId

case class Target(id: TargetId,
                  projectId: ProjectId,
                  name: String,
                  keywords: String)
