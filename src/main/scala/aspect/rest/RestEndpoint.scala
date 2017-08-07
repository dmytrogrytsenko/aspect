package aspect.rest

import java.util.concurrent.TimeUnit

import akka.actor.SupervisorStrategy
import akka.actor.SupervisorStrategy.stoppingStrategy
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.util.Timeout
import aspect.common.Messages.Start
import aspect.common.actors.{BaseActor, NodeSingleton1}
import aspect.common.config.Settings
import aspect.routes.{ProjectRoutes, TargetRoutes, UserRoutes, WebRoutes}
import com.typesafe.config.Config

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

object RestEndpoint extends NodeSingleton1[RestEndpoint, RestEndpointSettings]

class RestEndpoint(settings: RestEndpointSettings) extends BaseActor
  with WebRoutes
  with UserRoutes
  with ProjectRoutes
  with TargetRoutes {

  implicit val materializer = ActorMaterializer()
  implicit val dispatcher: ExecutionContextExecutor = context.dispatcher
  implicit val timeout: Timeout = Timeout(settings.defaultTimeout)

  val routes: Route = userRoutes ~ projectRoutes ~ webRoutes ~ targetRoutes

  def receive: Receive = {
    case Start =>
      Http(context.system).bindAndHandle(routes, settings.interface, settings.port)
  }

  override def supervisorStrategy: SupervisorStrategy = stoppingStrategy
}

case class RestEndpointSettings(config: Config) extends Settings {
  val interface: String = config.getString("interface")
  val port: Int = config.getInt("port")
  val defaultTimeout: FiniteDuration = config.getDuration("defaultTimeout", TimeUnit.MILLISECONDS).milliseconds
}
