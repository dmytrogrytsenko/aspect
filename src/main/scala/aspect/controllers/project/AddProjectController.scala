package aspect.controllers.project

import akka.actor.Props
import aspect.domain.UserId
import aspect.rest.models.AddProjectData

object AddProjectController {
  def props(userId: UserId, data: AddProjectData): Props = ???
}
