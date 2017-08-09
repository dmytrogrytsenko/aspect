package aspect.common.actors

import akka.actor._
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import aspect.common.extensions.AkkaExtensions._
import aspect.common.Messages.Start
import aspect.common.uuid

import scala.reflect.ClassTag

trait CustomClusterSingleton[TActor] extends CustomNodeSingleton {
  protected def createManager(role: Option[String], props: Props)
                             (implicit context: ActorContext): ActorRef =
    createTypedActor[ClusterSingletonManager](role, props)

  protected def createTypedManager(role: Option[String], args: Any*)
                                  (implicit actorTag: ClassTag[TActor], context: ActorContext): ActorRef =
    createManager(role, Props(actorTag.runtimeClass, args: _*))
}

trait ClusterSingleton[TActor] extends CustomClusterSingleton[TActor] {
  def create(role: Option[String])
            (implicit actorTag: ClassTag[TActor], context: ActorContext): ActorRef =
    createTypedManager(role)
}

trait ClusterSingleton1[TActor, TArg] extends CustomClusterSingleton[TActor] {
  def create(role: Option[String], arg: TArg)
            (implicit actorTag: ClassTag[TActor], context: ActorContext): ActorRef =
    createTypedManager(role, arg)
}

trait ClusterSingleton2[TActor, TArg1, TArg2] extends CustomClusterSingleton[TActor] {
  def create(role: Option[String], arg1: TArg1, arg2: TArg2)
            (implicit actorTag: ClassTag[TActor], context: ActorContext): ActorRef =
    createTypedManager(role, arg1, arg2)
}

class ClusterSingletonManager(role: Option[String], props: Props) extends BaseActor with Stash {
  val cluster = Cluster(context.system)

  override def preStart(): Unit =
    cluster.subscribe(self, InitialStateAsEvents, classOf[LeaderChanged], classOf[RoleLeaderChanged])

  override def postStop(): Unit =
    cluster.unsubscribe(self)

  override def aroundReceive(body: Receive, msg: Any): Unit = msg match {
    case Start =>
    case LeaderChanged(_) => if (role.isEmpty) changeLeader()
    case RoleLeaderChanged(receivedRole, _) => if (role.contains(receivedRole)) changeLeader()
    case _ => super.aroundReceive(body, msg)
  }

  def receive: Receive = waitingForLeader

  def waitingForLeader: Receive = {
    case _ => stash()
  }

  def leader(destination: ActorRef): Receive = {
    case msg => destination forward msg
  }

  def router(destination: ActorSelection): Receive = {
    case msg => destination forward msg
  }

  def changeLeader(): Unit = {
    context.children.foreach(_ ! PoisonPill)
    leaderNode match {
      case None => become(waitingForLeader)
      case Some(address) if address == cluster.selfAddress => become(leader(props.create(uuid.toString)))
      case Some(address) => become(router(self.on(address)))
    }
    unstashAll()
  }

  def leaderNode: Option[Address] = role.fold(cluster.state.leader)(cluster.state.roleLeader)
}
