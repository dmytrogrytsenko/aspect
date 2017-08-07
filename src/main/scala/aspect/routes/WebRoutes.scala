package aspect.routes

import akka.http.scaladsl.server.Route
import aspect.rest.Routes

trait WebRoutes extends Routes {

  val webRoutes: Route =
    get {
      path("") {
        pathEndOrSingleSlash {
          getFromResource("web/index.html")
        }
      }
    } ~
  get {
    getFromResourceDirectory("web")
  }

}
