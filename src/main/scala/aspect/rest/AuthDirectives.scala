package aspect.rest

import akka.http.scaladsl.model.headers.{HttpChallenge, HttpCredentials}
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.directives.{AuthenticationDirective, AuthenticationResult}
import akka.http.scaladsl.util.FastFuture
import aspect.domain.{SessionToken, UserId}

import scala.concurrent.{ExecutionContextExecutor, Future}

trait AuthDirectives extends Directives {

  implicit def dispatcher: ExecutionContextExecutor

  val challenge: HttpChallenge = HttpChallenge.apply(scheme = "", realm = "")

  def authenticate[T](authenticator: Option[HttpCredentials] ⇒ Future[AuthenticationResult[T]]): AuthenticationDirective[T] =
    authenticateOrRejectWithChallenge(authenticator)

  def getUserIdByToken(token: String): Future[Option[UserId]] = ???
  //    SessionRepository.endpoint ? GetSession(token) flatMap normalizeAskResult flatMap {
  //      case SessionFound(session) =>
  //        SessionRepository.endpoint ? Activity(token) flatMap normalizeAskResult map { _ => Some(session.userId) }
  //      case SessionNotFound(`token`) => Future { None }
  //    }

  def tokenAuthenticator(credentials: Option[HttpCredentials]): Future[AuthenticationResult[SessionToken]] = {
    credentials match {
      case Some(c) => FastFuture.successful(AuthenticationResult.success(SessionToken(c.token())))
      case None => FastFuture.successful(AuthenticationResult.failWithChallenge(challenge))
    }
  }

  def userAuthenticator(credentials: Option[HttpCredentials]): Future[AuthenticationResult[UserId]] = {
    credentials match {
      case Some(c) => getUserIdByToken(c.value) map {
        case Some(userId) => AuthenticationResult.success(userId)
        case None => AuthenticationResult.failWithChallenge(challenge)
      }
      case None => FastFuture.successful(AuthenticationResult.failWithChallenge(challenge))
    }
  }

}
