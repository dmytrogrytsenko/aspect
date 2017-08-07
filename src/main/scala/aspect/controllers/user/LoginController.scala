package aspect.controllers.user

import akka.actor.Props
import aspect.rest.models.LoginData

object LoginController {
  def props(data: LoginData): Props = ???
}
