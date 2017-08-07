package aspect.common.actors

import akka.actor.{ActorRef, ReceiveTimeout, Status}
import akka.event.LoggingReceive
import aspect.common.Messages.Start

import scala.concurrent.TimeoutException
import scala.concurrent.duration._
import scala.util.Try

trait Operation extends BaseActor {

  val receiveTimeout: FiniteDuration = 5.seconds

  var started: Boolean = false
  var originalSender: ActorRef = _

  context.setReceiveTimeout(receiveTimeout)

  def answer(msg: Any): Unit = originalSender ! msg

  def complete(msg: Any): Unit = {
    answer(msg)
    stop()
  }

  def failure(exception: Throwable): Unit = complete(Status.Failure(exception))

  override def aroundReceive(receive: Receive, msg: Any): Unit =
    super.aroundReceive(handle(receive), msg)

  def handle(body: Receive): Receive = LoggingReceive {
    case Start if !started =>
      originalSender = sender()
      if (body.isDefinedAt(Start)) answerableHandle(body, Start)
      started = true
    case msg if body.isDefinedAt(msg) => answerableHandle(body, msg)
    case msg: Status.Failure => complete(msg)
    case ReceiveTimeout => failure(new TimeoutException())
  }

  def answerableHandle(body: Receive, msg: Any): Try[Unit] =
    Try { body(msg) } recover { case e => failure(e) }
}