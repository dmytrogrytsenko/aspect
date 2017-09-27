package aspect.rest

import akka.actor.{ReceiveTimeout, Status}
import akka.http.scaladsl.model.StatusCodes.OK
import aspect.common.actors.Operation
import aspect.rest.RestErrors.{ErrorResult, RestException}

import scala.util.Failure

trait Controller extends Operation {
  override def complete(msg: Any): Unit = msg match {
    case Failure(_) => super.complete(msg)
    case _: Status.Failure => super.complete(msg)
    case ReceiveTimeout => super.complete(msg)
    case _: Throwable => super.complete(msg)
    case _ => super.complete (OK -> msg)
  }

  def failure(result: ErrorResult): Unit = failure(new RestException(result))
}
