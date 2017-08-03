package aspect.domain

import java.time.LocalDateTime

import aspect.common.Crypt.Sha256
import aspect.common.{EntityId, EntityIdCompanion}

trait UserId extends EntityId
object UserId extends EntityIdCompanion[UserId]

case class User(id: UserId,
                name: String,
                passwordHash: Sha256,
                email: String,
                firstName: String,
                lastName: String,
                createdAt: LocalDateTime)
