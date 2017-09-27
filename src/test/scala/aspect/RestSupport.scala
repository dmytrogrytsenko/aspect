package aspect

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.headers.{Authorization, GenericHttpCredentials}
import akka.http.scaladsl.model.{HttpMethod, HttpRequest, HttpResponse, StatusCodes}
import akka.stream.Materializer
import aspect.common.extensions.FutureExtensions
import aspect.domain.{ProjectId, SessionToken, TargetId}
import aspect.rest.JsonProtocol
import aspect.rest.RestErrors.{ErrorResult, RestException}
import aspect.rest.models._
import org.scalatest.exceptions.TestFailedException
import org.scalatest.Matchers
import spray.json._

import scala.concurrent.duration._

trait RestSupport extends Matchers with JsonProtocol with FutureExtensions {

  implicit val system: ActorSystem
  implicit def materializer: Materializer

  object Rest {

    val baseUrl = "http://localhost:8888"

    def login(data: LoginData): HttpResponse =
      POST(s"$baseUrl/login").withBody(data).execute

    def logout(token: SessionToken = null): HttpResponse =
      POST(s"$baseUrl/logout").withAuth(token).execute

    def getProfile(token: SessionToken = null): HttpResponse =
      GET(s"$baseUrl/profile").withAuth(token).execute

    def getProjects(token: SessionToken = null): HttpResponse =
      GET(s"$baseUrl/projects").withAuth(token).execute

    def getProject(projectId: ProjectId, token: SessionToken = null): HttpResponse =
      GET(s"$baseUrl/projects/${projectId.value}").withAuth(token).execute

    def addProject(data: AddProjectData, token: SessionToken = null): HttpResponse =
      POST(s"$baseUrl/projects").withAuth(token).withBody(data).execute

    def removeProject(projectId: ProjectId, token: SessionToken = null): HttpResponse =
      DELETE(s"$baseUrl/projects/${projectId.value}").withAuth(token).execute

    def updateProject(projectId: ProjectId, data: UpdateProjectData, token: SessionToken = null): HttpResponse =
      PUT(s"$baseUrl/projects/${projectId.value}").withAuth(token).withBody(data).execute

    def getTargets(projectId: ProjectId, token: SessionToken = null): HttpResponse =
      GET(s"$baseUrl/targets?projectId=${projectId.value}").withAuth(token).execute

    def getTarget(targetId: TargetId, token: SessionToken = null): HttpResponse =
      GET(s"$baseUrl/targets/${targetId.value}").withAuth(token).execute

    def addTarget(data: AddTargetData, token: SessionToken = null): HttpResponse =
      POST(s"$baseUrl/targets").withAuth(token).withBody(data).execute

    def removeTarget(targetId: TargetId, token: SessionToken = null): HttpResponse =
      DELETE(s"$baseUrl/targets/${targetId.value}").withAuth(token).execute

    def updateTarget(targetId: TargetId, data: UpdateTargetData, token: SessionToken = null): HttpResponse =
      PUT(s"$baseUrl/targets/${targetId.value}").withAuth(token).withBody(data).execute

  }

  implicit class HttpMethodOps(method: HttpMethod) {
    def apply(url: String): HttpRequest =
      HttpRequest().withMethod(method).withUri(url)
  }

  implicit class HttpRequestOps(request: HttpRequest) {
    def withAuth(token: SessionToken): HttpRequest = {
      if (token == null) request
      else request.withHeaders(Authorization(GenericHttpCredentials(token.value, "")))
    }

    def withBody[T](data: T)(implicit writer: JsonWriter[T]): HttpRequest =
      request.withEntity(`application/json`, data.toJson.prettyPrint)

    def execute: HttpResponse = {
      Http().singleRequest(request).await(5.seconds)
    }
  }

  implicit class HttpResponseOps(response: HttpResponse) {

    def body: String = response.entity.toStrict(5.seconds).await(5.seconds).data.utf8String

    def shouldBeOK() {
      response.status shouldBe StatusCodes.OK
    }

    def to[T: JsonReader]: T = {
      try {
        response.shouldBeOK()
      } catch {
        case _: TestFailedException =>
          throw new RestException(response.toErrorResult)
      }
      response.body.parseJson.convertTo[T]
    }

    def toErrorResult: ErrorResult = {
      val result = response.body.parseJson.convertTo[ErrorResult]
      response.status shouldBe result.status
      result
    }
  }
}
