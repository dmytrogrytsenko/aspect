package aspect.common.actors

import akka.cluster.{Cluster, Member}
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberRemoved, MemberUp}
import akka.routing.ConsistentHash
import aspect.common.Messages.Start
import aspect.common.actors.NodeRings.{GetNodes, GetShards, Shards}
import aspect.common.{Role, Shard}
import aspect.common.config.Settings
import com.typesafe.config.Config

object NodeRings extends NodeSingleton1[NodeRings, NodeRingsSettings] {
  case class GetShards(role: Role)
  case class Shards(role: Role, shards: Set[Shard])

  case class GetNodes(shard: Shard, maxSlaveCount: Int = 0)
  case class Nodes(shard: Shard, master: Member, slaves: Set[Member])
}

class NodeRings(settings: NodeRingsSettings) extends BaseActor {
  type Ring = ConsistentHash[Node]

  val VirtualNodesFactor = 1024

  val cluster = Cluster(context.system)

  case class Node(member: Member) {
    override val toString: String = member.uniqueAddress.longUid.toString
    val roles: Set[Role] = member.roles.map(Role.apply)
  }

  def receive: Receive = {
    case Start =>
      cluster.subscribe(self, classOf[MemberUp], classOf[MemberRemoved])
      become(working(Map.empty, Map.empty))
  }

  def working(roleToRing: Map[Role, Ring],
              roleToShards: Map[Role, Set[Shard]]): Receive = {

    case _: CurrentClusterState =>
      become(working(Map.empty, Map.empty))

    case MemberUp(member) =>
      lazy val node = Node(member)
      val updatedRoleToRings = roleToRing.map {
        case (role, ring) if node.roles.contains(role) => role -> ring.add(node)
        case x => x
      }
      become(working(updatedRoleToRings, roleToShards -- node.roles))

    case MemberRemoved(member, _) =>
      lazy val node = Node(member)
      val updatedRoleToRings = roleToRing.map {
        case (role, ring) if node.roles.contains(role) => role -> ring.remove(node)
        case x => x
      }
      become(working(updatedRoleToRings, roleToShards -- node.roles))

    case GetShards(role) => ???
      //val (shards, updated = getShards
      //roleToShards.get(role) match {
      //  case Some(shards) =>
      //}
      //become(working(updatedRoleToRing, updatedRoleToShards))
      //sender ! Shards(role, shards)

      case GetNodes(shard, maxSlaveCount) => ???
  }

  override def postStop(): Unit = {
    cluster.unsubscribe(self)
    super.postStop()
  }

  def createRing(role: Role): Ring = {
    val nodes = cluster.state.members.map(Node).filter(_.roles.contains(role))
    ConsistentHash[Node](nodes, VirtualNodesFactor)
  }
}

case class NodeRingsSettings(config: Config) extends Settings
