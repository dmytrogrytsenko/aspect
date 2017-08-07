package aspect.controllers.user

import akka.actor.Props
import aspect.domain.UserId

object GetProfileController {
  def props(userId: UserId): Props = ???
}
