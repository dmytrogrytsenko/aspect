package aspect.domain

import java.time.LocalDateTime

import aspect.common.{EntityId, EntityIdCompanion, utc, uuid}

case class SessionToken(value: String) extends EntityId

object SessionToken extends EntityIdCompanion[SessionToken]

case class Session(token: SessionToken,
                   userId: UserId,
                   createdAt: LocalDateTime,
                   lastActivityAt: LocalDateTime)

object Session {
  def create(userId: UserId): Session =
    Session(SessionToken(uuid.toString), userId, utc, utc)
}
