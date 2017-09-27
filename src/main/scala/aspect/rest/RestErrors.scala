package aspect.rest

import akka.http.scaladsl.model.{StatusCode, StatusCodes}

object RestErrors {
  type ErrorCode = String

  case class ErrorResult(status: StatusCode, code: ErrorCode, message: String)

  class RestException(val result: ErrorResult)
    extends RuntimeException(s"${result.status.intValue} (${result.status.reason}) ${result.code} ${result.message}")

  //400
  object BadRequest {
    def validation(message: String) = ErrorResult(StatusCodes.BadRequest, "VALIDATION", message)

    object Validation {
      def requiredMemberEmpty(name: String): ErrorResult =
        validation(s"The request content validation is failed: Required member '$name' is empty")
    }
  }

  //401
  object Unauthorized {
    val credentialsMissing = ErrorResult(StatusCodes.Unauthorized, "CREDENTIALS_MISSING", "The resource requires authentication, which was not supplied with the request")
    val credentialsRejected = ErrorResult(StatusCodes.Unauthorized, "CREDENTIALS_REJECTED", "The supplied authentication is invalid")
  }

  //403
  object Forbidden {
    val accessDenied = ErrorResult(StatusCodes.Forbidden, "ACCESS_DENIED","The supplied authentication is not authorized to access this resource")
  }

  //404
  object NotFound {
    val resourceNotFound = ErrorResult(StatusCodes.NotFound, "RESOURCE_NOT_FOUND", "The requested resource could not be found")
    val projectNotFound = ErrorResult(StatusCodes.NotFound, "PROJECT_NOT_FOUND", "Project is not found")
    val targetNotFound = ErrorResult(StatusCodes.NotFound, "TARGET_NOT_FOUND", "Target is not found")
  }

  //500
  object InternalServerError {
    val default = ErrorResult(StatusCodes.InternalServerError, "INTERNAL_SERVER_ERROR", "There was an internal server error")
    val timeout = ErrorResult(StatusCodes.InternalServerError, "TIMEOUT", "The request processing is timed out")
  }
}
