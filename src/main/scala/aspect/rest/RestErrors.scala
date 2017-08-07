package aspect.rest

import akka.http.scaladsl.model.{StatusCode, StatusCodes}

trait RestErrors {
  type ErrorCode = String

  case class ErrorResult(status: StatusCode, code: ErrorCode, message: String)

  class RestException(val result: ErrorResult)
    extends RuntimeException(s"${result.status.intValue} (${result.status.reason}}) ${result.code} ${result.message}")

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
    def credentialsMissing = ErrorResult(StatusCodes.Unauthorized, "CREDENTIALS_MISSING", "The resource requires authentication, which was not supplied with the request.")
    def credentialsRejected = ErrorResult(StatusCodes.Unauthorized, "CREDENTIALS_REJECTED", "The supplied authentication is invalid.")
  }

  //403
  object Forbidden {
    def accessDenied = ErrorResult(StatusCodes.Forbidden, "ACCESS_DENIED","The supplied authentication is not authorized to access this resource.")
  }

  //404
  object NotFound {
    def projectNotFound = ErrorResult(StatusCodes.NotFound, "PROJECT_NOT_FOUND", "Project is not found.")
    def targetNotFound = ErrorResult(StatusCodes.NotFound, "TARGET_NOT_FOUND", "Target is not found.")
  }
}
