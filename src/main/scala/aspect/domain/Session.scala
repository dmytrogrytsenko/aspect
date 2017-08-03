package aspect.domain

import java.time.LocalDateTime

import aspect.common.{EntityId, EntityIdCompanion}

trait SessionToken extends EntityId
object SessionToken extends EntityIdCompanion[SessionToken]

case class Session(token: SessionToken,
                   userId: UserId,
                   createdAt: LocalDateTime,
                   lastActivityAt: LocalDateTime)
