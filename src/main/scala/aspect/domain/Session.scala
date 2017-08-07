package aspect.domain

import java.time.LocalDateTime

import aspect.common.{EntityId, now, uuid}

case class SessionToken(value: String) extends EntityId

case class Session(token: SessionToken,
                   userId: UserId,
                   createdAt: LocalDateTime,
                   lastActivityAt: LocalDateTime)

object Session {
  def create(userId: UserId): Session =
    Session(SessionToken(uuid.toString), userId, now, now)
}
