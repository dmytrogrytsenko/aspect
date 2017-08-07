package aspect.domain

import java.time.LocalDateTime

import aspect.common.EntityId

case class SessionToken(value: String) extends EntityId

case class Session(token: SessionToken,
                   userId: UserId,
                   createdAt: LocalDateTime,
                   lastActivityAt: LocalDateTime)
