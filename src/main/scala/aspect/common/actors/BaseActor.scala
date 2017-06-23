package aspect.common.actors

import akka.actor.{Cancellable, ActorRef, ActorLogging, Actor}
import akka.event.LoggingReceive

import scala.concurrent.duration.FiniteDuration

trait BaseActor extends Actor with ActorLogging {

  override implicit val log = akka.event.Logging(this)

  override def aroundReceive(receive: Receive, msg: Any) : Unit =
    super.aroundReceive(LoggingReceive(receive), msg)

  lazy val parent: ActorRef = context.parent

  def become(behavior: Receive, discardOld: Boolean = true): Unit = context.become(behavior, discardOld)

  def unbecome(): Unit = context.unbecome()

  def stop(): Unit = context.stop(self)

  def watch(subject: ActorRef): ActorRef = context.watch(subject)

  def unwatch(subject: ActorRef): ActorRef = context.unwatch(subject)

  def scheduleOnce(delay: FiniteDuration,
                   message: Any,
                   receiver: ActorRef = self): Cancellable = {
    import context.dispatcher
    context.system.scheduler.scheduleOnce(delay, receiver, message)
  }

  def schedule(initialDelay: FiniteDuration,
               interval: FiniteDuration,
               message: Any): Cancellable = {
    import context.dispatcher
    context.system.scheduler.schedule(initialDelay, interval, self, message)
  }

  def schedule(interval: FiniteDuration,
               message: Any): Cancellable =
    schedule(interval, interval, message)
}
