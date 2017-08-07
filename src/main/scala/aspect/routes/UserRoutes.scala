package aspect.routes

import akka.http.scaladsl.server.Route
import aspect.common.Messages.Done
import aspect.controllers.user.{GetProfileController, LoginController, LogoutController}
import aspect.rest.models.{LoginData, LoginResult, ProfileResult}
import aspect.rest.Routes

trait UserRoutes extends Routes {

  val userRoutes: Route = routeLogin ~ routeLogout ~ routeGetProfile

  def routeLogin: Route =
    (post & path("login")) {
      entity(as[LoginData]) { data =>
        validate(data.login.nonEmpty, BadRequest.Validation.requiredMemberEmpty("login").message) {
          complete {
            LoginController.props(data).execute[LoginResult]
          }
        }
      }
    }

  def routeLogout: Route =
    (post & path("logout")) {
      (authenticate(tokenAuthenticator) & pass) { token =>
        complete {
          LogoutController.props(token).execute[Done]
        }
      }
    }

  def routeGetProfile: Route =
    (get & path("profile")) {
      (authenticate(userAuthenticator) & pass) { userId =>
        complete {
          GetProfileController.props(userId).execute[ProfileResult]
        }
      }
    }
}
