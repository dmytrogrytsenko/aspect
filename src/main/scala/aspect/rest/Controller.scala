package aspect.rest

import aspect.common.actors.Operation

trait Controller extends Operation with RestErrors {
  def failure(result: ErrorResult): Unit = failure(new RestException(result))
}
