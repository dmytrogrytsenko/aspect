package aspect.repositories

import akka.pattern.pipe
import aspect.common.Messages.Start
import aspect.common.actors.{BaseActor, NodeSingleton}
import aspect.common.mongo.MongoDatabase
import aspect.domain.{Session, SessionToken}
import aspect.mongo.{BsonProtocol, SessionCollection}

object SessionRepository extends NodeSingleton[SessionRepository] {
  case class AddSession(session: Session)
  case class SessionAdded(token: SessionToken)

  case class RemoveSession(token: SessionToken)
  case class SessionRemoved(token: SessionToken)

  case class GetSession(token: SessionToken)
  case class SessionFound(session: Session)
  case class SessionNotFound(token: SessionToken)

  case class Activity(token: SessionToken)
  case class ActivityCompleted(token: SessionToken)
}

class SessionRepository extends BaseActor with BsonProtocol {
  import SessionRepository._
  import context.dispatcher

  val collection = new SessionCollection(MongoDatabase.db)

  def receive: Receive = {
    case Start => collection.ensureIndexes

    case AddSession(session) =>
      collection.add(session) map (_ => SessionAdded(session.token)) pipeTo sender

    case RemoveSession(token) =>
      collection.remove(token) map (_ => SessionRemoved(token)) pipeTo sender

    case GetSession(token) =>
      collection.get(token) map {
        case Some(session) => SessionFound(session)
        case None => SessionNotFound(token)
      } pipeTo sender

    case Activity(token) =>
      collection.activity(token) map { _ =>
        ActivityCompleted(token)
      } pipeTo sender
  }
}
