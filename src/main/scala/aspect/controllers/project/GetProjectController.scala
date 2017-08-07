package aspect.controllers.project

import akka.actor.Props
import aspect.domain.{ProjectId, UserId}

object GetProjectController {
  def props(userId: UserId, projectId: ProjectId): Props = ???
}
