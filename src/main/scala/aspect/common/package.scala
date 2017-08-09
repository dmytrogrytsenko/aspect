package aspect

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID
import java.util.concurrent.TimeUnit

import scala.concurrent.duration.FiniteDuration

package object common {
  def now: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC"))

  def uuid: UUID = UUID.randomUUID()

  implicit class LocalDateTimeOps(value: LocalDateTime) {
    def >(operand: LocalDateTime): Boolean = value isAfter operand
    def <(operand: LocalDateTime): Boolean = value isBefore operand
    def >=(operand: LocalDateTime): Boolean = !value.isBefore(operand)
    def <=(operand: LocalDateTime): Boolean = !value.isAfter(operand)
    def +(operand: FiniteDuration): LocalDateTime = value plusNanos operand.toNanos
    def -(operand: FiniteDuration): LocalDateTime = value plusNanos operand.toNanos
    def -(operand: LocalDateTime): FiniteDuration = FiniteDuration(value.getNano - operand.getNano, TimeUnit.NANOSECONDS)
  }
}
