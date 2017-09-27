package aspect.domain

import java.time.LocalDateTime

import aspect.common.{EntityId, EntityIdCompanion}

case class UserId(value: String) extends EntityId

object UserId extends EntityIdCompanion[UserId]

case class User(id: UserId,
                name: String,
                password: String,
                email: String,
                firstName: Option[String],
                lastName: Option[String],
                createdAt: LocalDateTime)
