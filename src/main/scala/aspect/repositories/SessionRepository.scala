package aspect.repositories

import aspect.common._
import aspect.common.actors.{BaseActor, NodeSingleton}
import aspect.domain.{Session, SessionToken}

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

class SessionRepository extends BaseActor {
  import SessionRepository._

  def receive: Receive = working(Nil)

  def working(sessions: List[Session]): Receive = {
    case AddSession(session) =>
      become(working(session +: sessions.filterNot(_.token == session.token)))
      sender ! SessionAdded(session.token)

    case RemoveSession(token) =>
      become(working(sessions.filterNot(_.token == token)))
      sender ! SessionRemoved(token)

    case GetSession(token) =>
      val result = sessions.find(_.token == token) match {
        case Some(session) => SessionFound(session)
        case None => SessionNotFound(token)
      }
      sender ! result

    case Activity(token) =>
      sessions.find(_.token == token).foreach { session =>
        become(working(session.copy(lastActivityAt = now) +: sessions.filterNot(_.token == token)))
      }
      sender ! ActivityCompleted(token)
  }
}
