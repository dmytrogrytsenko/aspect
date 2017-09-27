package aspect.common.actors

import java.time.LocalDateTime

import akka.actor._
import aspect.common._
import aspect.common.extensions.AkkaExtensions._
import aspect.common.Messages.Start
import aspect.common.config.Settings
import com.typesafe.config.Config

import scala.concurrent.duration._

object Repeater {
  def props(settings: RepeaterSettings, factory: Config => Props): Props =
    Props(classOf[Repeater], settings, factory)
}

class Repeater(settings: RepeaterSettings, factory: Config => Props) extends BaseActor {
  case object Execute

  def receive: PartialFunction[Any, Unit] = {
    case Start => wait(utc + settings.initialInterval)
  }

  def wait(startTime: LocalDateTime): Unit = {
    val justNow = utc
    if (startTime >= justNow) self !! Execute
    else scheduleOnce(justNow - startTime, Execute)
    context.setReceiveTimeout(Duration.Undefined)
    become(waiting)
  }

  def waiting: Receive = {
    case Execute =>
      val operation = watch(factory(settings.operation).create("operation"))
      context.setReceiveTimeout(settings.timeout)
      become(executing(utc, operation))
  }

  def executing(startTime: LocalDateTime, operation: ActorRef): Receive = {
    case Terminated(actor) if actor == operation => wait(startTime + settings.interval)
    case ReceiveTimeout => operation !! PoisonPill
  }
}

trait RepeaterSettings extends Settings {
  val initialInterval: FiniteDuration = get[Option[FiniteDuration]]("initialInterval").getOrElse(Duration.Zero)
  val interval: FiniteDuration = get[FiniteDuration]("interval")
  val timeout: FiniteDuration = get[FiniteDuration]("timeout")
  val operation: Config = get[Config]("operation")
}
