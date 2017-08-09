package aspect.rest

import akka.http.scaladsl.model.StatusCodes.OK
import aspect.common.actors.Operation

trait Controller extends Operation with RestErrors {
  override def complete(msg: Any): Unit = super.complete(OK -> msg)
  def failure(result: ErrorResult): Unit = failure(new RestException(result))
}
