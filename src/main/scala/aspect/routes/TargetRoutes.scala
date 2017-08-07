package aspect.routes

import akka.http.scaladsl.server.Route
import aspect.common.Messages.Done
import aspect.controllers.target._
import aspect.domain.ProjectId
import aspect.rest.models._
import aspect.rest.Routes

trait TargetRoutes extends Routes {

  val targetRoutes: Route = routeGetTargets ~ routeGetTarget ~ routeAddTarget ~ routeRemoveTarget ~ routeUpdateTarget

  def routeGetTargets: Route =
    (get & path("targets")) {
      (authenticate(userAuthenticator) & pass) { userId =>
        parameters("projectId".as[ProjectId]) { projectId =>
          complete {
            GetTargetsController.props(userId, projectId).execute[TargetListResult]
          }
        }
      }
    }

  def routeGetTarget: Route =
    (get & path("targets" / TargetIdSegment)) { targetId =>
      (authenticate(userAuthenticator) & pass) { userId =>
        complete {
          GetTargetController.props(userId, targetId).execute[TargetResult]
        }
      }
    }

  def routeAddTarget: Route =
    (post & path("targets")) {
      (authenticate(userAuthenticator) & pass) { userId =>
        entity(as[AddTargetData]) { data =>
          validate(data.name.nonEmpty, BadRequest.Validation.requiredMemberEmpty("name").message) {
            complete {
              AddTargetController.props(userId, data).execute[AddTargetResult]
            }
          }
        }
      }
    }

  def routeRemoveTarget: Route =
    (delete & path("targets" / TargetIdSegment)) { targetId =>
      (authenticate(userAuthenticator) & pass) { userId =>
        complete {
          RemoveTargetController.props(userId, targetId).execute[Done]
        }
      }
    }

  def routeUpdateTarget: Route =
    (put & path("targets" / TargetIdSegment)) { targetId =>
      (authenticate(userAuthenticator) & pass) { userId =>
        entity(as[UpdateTargetData]) { data =>
          validate(data.name.forall(_.nonEmpty), BadRequest.Validation.requiredMemberEmpty("name").message) {
            complete {
              UpdateTargetController.props(userId, targetId, data).execute[Done]
            }
          }
        }
      }
    }
}