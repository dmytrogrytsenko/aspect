package aspect.controllers.project

import akka.actor.Props
import aspect.domain.{ProjectId, UserId}

object RemoveProjectController {
  def props(userId: UserId, projectId: ProjectId): Props = ???
}
