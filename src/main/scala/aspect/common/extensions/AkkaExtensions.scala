package aspect.common.extensions

import akka.actor._
import akka.cluster.{Cluster, Member, MemberStatus}
import akka.event.LoggingAdapter
import akka.pattern.{PipeToSupport, ask}
import akka.util.Timeout
import aspect.common.Messages.Start

import scala.concurrent.{ExecutionContext, Future, TimeoutException}
import scala.reflect.ClassTag
import scala.util.Failure

object AkkaExtensions extends AkkaExtensions

trait AkkaExtensions {
  def normalizeAskResult(msg: Any): Future[Any] = msg match {
    case Failure(exception) => Future.failed(exception)
    case Status.Failure(exception) => Future.failed(exception)
    case ReceiveTimeout => Future.failed(new TimeoutException())
    case result => Future.successful(result)
  }

  implicit class RichProps(props: Props) {
    def create(implicit context: ActorContext): ActorRef = {
      val actor = context.actorOf(props)
      actor ! Start
      actor
    }

    def create(name: String)(implicit context: ActorContext): ActorRef = {
      val actor = context.actorOf(props, name)
      actor ! Start
      actor
    }

    def execute[T](implicit tag: ClassTag[T],
                   context: ActorContext,
                   executionContext: ExecutionContext,
                   timeout: Timeout): Future[T] =
      (context.actorOf(props) ? Start flatMap normalizeAskResult).mapTo[T]
  }

  implicit class RichCluster(cluster: Cluster) {
    def leader(role: Option[String] = None): Option[Address] =
      role.fold(cluster.state.leader)(cluster.state.roleLeader)

    def members(role: Option[String] = None): List[Member] =
      cluster.state.members.toList
        .filter(_.status == MemberStatus.up)
        .filter(member => role.fold(true)(member.hasRole))
        .sortWith(_ isOlderThan _)
  }

  implicit class RichAddress(address: Address) {
    def /(elements: Iterable[String]): ActorPath =
      elements.foldLeft(RootActorPath(address).asInstanceOf[ActorPath])(_ / _)

    def selfClone(implicit context: ActorContext): ActorSelection =
      context.actorSelection(RootActorPath(address) / context.self.path.elements)
  }

  implicit class RichActorRef(underlying: ActorRef) {
    def on(address: Address)(implicit context: ActorContext): ActorSelection =
      context.actorSelection(RootActorPath(address) / underlying.path.elements)

    def !!(message: Any)(implicit sender: ActorRef = Actor.noSender, log: LoggingAdapter): Unit = {
      log.debug(s"send to ${underlying.path.toStringWithoutAddress} message $message")
      underlying.!(message)(sender)
    }

    def >>(message: Any)(implicit context: ActorContext, log: LoggingAdapter): Unit = {
      log.debug(s"forward to ${underlying.path.toStringWithoutAddress} message $message")
      underlying.forward(message)
    }

    def ??(message: Any)(implicit timeout: Timeout, log: LoggingAdapter): Future[Any] = {
      log.debug(s"ask ${underlying.path.toStringWithoutAddress} for $message")
      underlying.?(message)(timeout)
    }
  }



  implicit class RichActorSelection(val underlying: ActorSelection) {
    def !!(message: Any)(implicit sender: ActorRef = Actor.noSender, log: LoggingAdapter): Unit = {
      log.debug(s"send to ${underlying.pathString} message $message")
      underlying.!(message)(sender)
    }

    def >>(message: Any)(implicit context: ActorContext, log: LoggingAdapter): Unit = {
      log.debug(s"forward to ${underlying.pathString} message $message")
      underlying.forward(message)
    }

    def ??(message: Any)(implicit timeout: Timeout, log: LoggingAdapter): Future[Any] = {
      log.debug(s"ask ${underlying.pathString} for $message")
      underlying.?(message)(timeout)
    }
  }
}
