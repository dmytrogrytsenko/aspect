package aspect.rest

import java.time.LocalDateTime

import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.model.Uri.Path.Segment
import akka.http.scaladsl.server.PathMatcher1
import akka.http.scaladsl.server.PathMatchers
import akka.http.scaladsl.unmarshalling.Unmarshaller
import aspect.common.Crypt._
import aspect.common.Messages.Done
import aspect.domain.{ProjectId, SessionToken, TargetId, UserId}
import aspect.rest.models._
import spray.json._

trait JsonProtocol extends DefaultJsonProtocol with RestErrors {

  val ProjectIdSegment: PathMatcher1[ProjectId] = PathMatchers.Segment.map(ProjectId)
  val TargetIdSegment: PathMatcher1[TargetId] = PathMatchers.Segment.map(TargetId)

  implicit val ProjectIdUnmarshaller: Unmarshaller[String, ProjectId] = Unmarshaller.strict[String, ProjectId](ProjectId)

  implicit object LocalDateTimeJsonFormat extends RootJsonFormat[LocalDateTime] {
    def write(datetime: LocalDateTime): JsValue = JsString(datetime.toString)
    def read(json: JsValue): LocalDateTime = json match {
      case JsString(x) => LocalDateTime.parse(x)
      case x => deserializationError("Expected LocalDateTime as JsString with format ISO-8601 uuuu-MM-dd'T'HH:mm:ss, but got " + x)
    }
  }

  implicit object StatusCodeJsonFormat extends RootJsonFormat[StatusCode] {
    def write(statusCode: StatusCode): JsValue = JsNumber(statusCode.intValue())
    def read(json: JsValue): StatusCode = json match {
      case JsNumber(x) => StatusCode.int2StatusCode(x.toInt)
      case x => deserializationError("Expected StatusCode as JsNumber, but got " + x)
    }
  }

  implicit object Sha256JsonFormat extends JsonFormat[Sha256] {
    def read(json: JsValue): Sha256 = json match {
      case JsString(value) => Sha256.parse(value)
      case _ => throw DeserializationException("Expected Sha256 as JsString")
    }
    def write(value: Sha256): JsValue = JsString(value.underlying.hex)
  }

  implicit object DoneJsonFormat extends RootJsonFormat[Done] {
    def write(value: Done): JsValue = JsString("")
    def read(json: JsValue): Done = json match {
      case JsString("") => Done
      case x => deserializationError("Expected Done as empty JsString, but got " + x)
    }
  }

  implicit object UserIdJsonFormat extends JsonFormat[UserId] {
    def read(json: JsValue): UserId = json match {
      case JsString(value) => UserId(value)
      case _ => throw DeserializationException("Expected UserId as JsString")
    }
    def write(value: UserId): JsValue = JsString(value.value)
  }

  implicit object ProjectIdJsonFormat extends JsonFormat[ProjectId] {
    def read(json: JsValue): ProjectId = json match {
      case JsString(value) => ProjectId(value)
      case _ => throw DeserializationException("Expected ProjectId as JsString")
    }
    def write(value: ProjectId): JsValue = JsString(value.value)
  }

  implicit object TargetIdJsonFormat extends JsonFormat[TargetId] {
    def read(json: JsValue): TargetId = json match {
      case JsString(value) => TargetId(value)
      case _ => throw DeserializationException("Expected TargetId as JsString")
    }
    def write(value: TargetId): JsValue = JsString(value.value)
  }

  implicit object SessionTokenJsonFormat extends JsonFormat[SessionToken] {
    def read(json: JsValue): SessionToken = json match {
      case JsString(value) => SessionToken(value)
      case _ => throw DeserializationException("Expected SessionToken as JsString")
    }
    def write(value: SessionToken): JsValue = JsString(value.value)
  }

  implicit val jsonError: RootJsonFormat[ErrorResult] = jsonFormat3(ErrorResult)

  implicit val jsonLoginData: RootJsonFormat[LoginData] = jsonFormat2(LoginData)
  implicit val jsonLoginResult: RootJsonFormat[LoginResult] = jsonFormat1(LoginResult.apply)
  implicit val jsonProfileResult: RootJsonFormat[ProfileResult] = jsonFormat6(ProfileResult.apply)

  implicit val jsonProjectItemResult: RootJsonFormat[ProjectItemResult] = jsonFormat3(ProjectItemResult.apply)
  implicit val jsonProjectListResult: RootJsonFormat[ProjectListResult] = jsonFormat1(ProjectListResult)
  implicit val jsonProjectResult: RootJsonFormat[ProjectUserResult] = jsonFormat2(ProjectUserResult.apply)
  implicit val jsonProjectUserResult: RootJsonFormat[ProjectResult] = jsonFormat4(ProjectResult.apply)
  implicit val jsonAddProjectData: RootJsonFormat[AddProjectData] = jsonFormat1(AddProjectData)
  implicit val jsonAddProjectResult: RootJsonFormat[AddProjectResult] = jsonFormat1(AddProjectResult)
  implicit val jsonUpdateProjectData: RootJsonFormat[UpdateProjectData] = jsonFormat1(UpdateProjectData)

  implicit val jsonTargetItemResult: RootJsonFormat[TargetItemResult] = jsonFormat3(TargetItemResult.apply)
  implicit val jsonTargetListResult: RootJsonFormat[TargetListResult] = jsonFormat1(TargetListResult)
  implicit val jsonTargetUserResult: RootJsonFormat[TargetUserResult] = jsonFormat2(TargetUserResult.apply)
  implicit val jsonTargetProjectResult: RootJsonFormat[TargetProjectResult] = jsonFormat3(TargetProjectResult.apply)
  implicit val jsonTargetResult: RootJsonFormat[TargetResult] = jsonFormat5(TargetResult.apply)
  implicit val jsonAddTargetData: RootJsonFormat[AddTargetData] = jsonFormat3(AddTargetData)
  implicit val jsonAddTargetResult: RootJsonFormat[AddTargetResult] = jsonFormat1(AddTargetResult)
  implicit val jsonUpdateTargetData: RootJsonFormat[UpdateTargetData] = jsonFormat2(UpdateTargetData)}
