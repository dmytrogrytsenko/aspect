package aspect.common.extensions

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object FutureExtensions extends FutureExtensions

trait FutureExtensions {
  implicit class RichFuture[T](val future: Future[T]) {
    def await(implicit timeout: Duration): T =
      Await.result(future, timeout)
  }
}
