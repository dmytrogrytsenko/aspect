package aspect.repositories

import akka.pattern.pipe
import aspect.common.Messages.Start
import aspect.common.actors.{BaseActor, NodeSingleton}
import aspect.common.mongo.MongoDatabase
import aspect.domain.{ProjectId, Target, TargetId}
import aspect.mongo.{BsonProtocol, TargetCollection}

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

class TargetRepository extends BaseActor with BsonProtocol {
  import TargetRepository._
  import context.dispatcher

  val collection = new TargetCollection(MongoDatabase.db)

  def receive: Receive = {
    case Start => collection.ensureIndexes

    case GetProjectTargets(projectId) =>
      collection.getProjectTargets(projectId) map (ProjectTargets(projectId, _)) pipeTo sender

    case FindTargetById(targetId) =>
      collection.get(targetId) map {
        case Some(target) => TargetFoundById(target)
        case None => TargetNotFoundById(targetId)
      } pipeTo sender

    case AddTarget(target) =>
      collection.add(target) map (_ => TargetAdded(target.id)) pipeTo sender

    case RemoveTarget(targetId) =>
      collection.remove(targetId) map (_ => TargetRemoved(targetId)) pipeTo sender

    case UpdateTarget(targetId, name, keywords) =>
      collection.update(targetId, name, keywords) map (_ => TargetUpdated(targetId)) pipeTo sender
  }
}