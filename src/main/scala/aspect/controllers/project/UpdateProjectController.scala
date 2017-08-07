package aspect.controllers.project

import akka.actor.Props
import aspect.domain.{ProjectId, UserId}
import aspect.rest.models.UpdateProjectData

object UpdateProjectController {
  def props(userId: UserId, projectId: ProjectId, data: UpdateProjectData): Props = ???
}
