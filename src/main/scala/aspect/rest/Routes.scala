package aspect.rest

import akka.actor.{ActorContext, Props, ReceiveTimeout, Status}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.server.Directives
import akka.pattern.ask
import akka.util.Timeout
import aspect.common.Messages.Start
import aspect.common.actors.BaseActor
import aspect.common.extensions.AkkaExtensions._

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future, TimeoutException}
import scala.reflect.ClassTag

trait Routes extends BaseActor
  with Directives
  with AuthDirectives
  with SprayJsonSupport
  with RestErrors
  with JsonProtocol {

  implicit def dispatcher: ExecutionContextExecutor
  implicit def timeout: Timeout

  implicit class RichProps(props: Props) {
    def execute[T](implicit tag: ClassTag[T],
                   context: ActorContext,
                   executionContext: ExecutionContext,
                   timeout: Timeout): Future[(StatusCode, T)] =
      (context.actorOf(props) ? Start flatMap normalizeAskResult).mapTo[(StatusCode, T)]
  }
}
