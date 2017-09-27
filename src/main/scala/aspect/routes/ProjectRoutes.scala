package aspect.routes

import akka.http.scaladsl.server.Route
import aspect.common.Messages.Done
import aspect.controllers.project._
import aspect.rest.RestErrors.BadRequest
import aspect.rest.models._
import aspect.rest.Routes

trait ProjectRoutes extends Routes {

  val projectRoutes: Route =
    routeGetProjects ~ routeGetProject ~ routeAddProject ~ routeRemoveProject ~ routeUpdateProject

  def routeGetProjects: Route =
    (get & path("projects")) {
      (authenticate(userAuthenticator) & pass) { userId =>
        complete {
          GetProjectsController.props(userId).execute[ProjectListResult]
        }
      }
    }

  def routeGetProject: Route =
    (get & path("projects" / ProjectIdSegment)) { projectId =>
      (authenticate(userAuthenticator) & pass) { userId =>
        complete {
          GetProjectController.props(userId, projectId).execute[ProjectResult]
        }
      }
    }

  def routeAddProject: Route =
    (post & path("projects")) {
      (authenticate(userAuthenticator) & pass) { userId =>
        entity(as[AddProjectData]) { data =>
          validate(data.name.nonEmpty, BadRequest.Validation.requiredMemberEmpty("name").message) {
            complete {
              AddProjectController.props(userId, data).execute[AddProjectResult]
            }
          }
        }
      }
    }

  def routeRemoveProject: Route =
    (delete & path("projects" / ProjectIdSegment)) { projectId =>
      (authenticate(userAuthenticator) & pass) { userId =>
        complete {
          RemoveProjectController.props(userId, projectId).execute[Done]
        }
      }
    }

  def routeUpdateProject: Route =
    (put & path("projects" / ProjectIdSegment)) { projectId =>
      (authenticate(userAuthenticator) & pass) { userId =>
        entity(as[UpdateProjectData]) { data =>
          validate(data.name.forall(_.nonEmpty), BadRequest.Validation.requiredMemberEmpty("name").message) {
            complete {
              UpdateProjectController.props(userId, projectId, data).execute[Done]
            }
          }
        }
      }
    }
}