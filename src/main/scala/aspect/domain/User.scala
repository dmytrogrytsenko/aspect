package aspect.domain

import java.time.LocalDateTime

import aspect.common.Crypt.Sha256
import aspect.common.EntityId

case class UserId(value: String) extends EntityId

case class User(id: UserId,
                name: String,
                passwordHash: Sha256,
                email: String,
                firstName: String,
                lastName: String,
                createdAt: LocalDateTime)
