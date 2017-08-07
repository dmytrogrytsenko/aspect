package aspect.rest

import akka.http.scaladsl.model.{StatusCode, StatusCodes}

trait RestErrors {
  type ErrorCode = String

  case class ErrorResult(status: StatusCode, code: ErrorCode, message: String)

  //400
  object BadRequest {
    def validation(message: String) = ErrorResult(StatusCodes.BadRequest, "VALIDATION", message)

    object Validation {
      def requiredMemberEmpty(name: String): ErrorResult =
        validation(s"The request content validation is failed: Required member '$name' is empty")
    }
  }
}
