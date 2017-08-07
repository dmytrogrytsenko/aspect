package aspect.repositories

import aspect.common.actors.{BaseActor, NodeSingleton}
import aspect.domain.{ProjectId, Target, TargetId}

object TargetRepository extends NodeSingleton[TargetRepository] {
  case class GetProjectTargets(projectId: ProjectId)
  case class ProjectTargets(projectId: ProjectId, targets: List[Target])

  case class FindTargetById(targetId: TargetId)
  case class TargetFoundById(target: Target)
  case class TargetNotFoundById(targetId: TargetId)

  case class AddTarget(target: Target)
  case class TargetAdded(targetId: TargetId)

  case class RemoveTarget(targetId: TargetId)
  case class TargetRemoved(targetId: TargetId)

  case class UpdateTarget(targetId: TargetId, name: Option[String], keywords: Option[String])
  case class TargetUpdated(targetId: TargetId)
}

class TargetRepository extends BaseActor {
  import TargetRepository._

  def receive: Receive = working(Nil)

  def working(targets: List[Target]): Receive = {
    case GetProjectTargets(projectId) =>
      val result = targets.filter(_.projectId == projectId)
      sender ! ProjectTargets(projectId, result)

    case FindTargetById(targetId) =>
      val result = targets.find(_.id == targetId) match {
        case Some(target) => TargetFoundById(target)
        case None => TargetNotFoundById(targetId)
      }
      sender ! result

    case AddTarget(target) =>
      become(working(target +: targets.filterNot(_.id == target.id)))
      sender ! TargetAdded(target.id)

    case RemoveTarget(targetId) =>
      become(working(targets.filterNot(_.id == targetId)))
      sender !  TargetRemoved(targetId)

    case UpdateTarget(targetId, name, keywords) =>
      targets.find(_.id == targetId).foreach { target =>
        val updatedTarget = target.copy(
          name = name.getOrElse(target.name),
          keywords = keywords.getOrElse(target.keywords))
        become(working(updatedTarget +: targets.filterNot(_.id == targetId)))
      }
      sender ! TargetUpdated(targetId)
  }
}