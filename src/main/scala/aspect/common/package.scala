package aspect

import java.time.temporal.ChronoUnit
import java.time.{LocalDateTime, ZoneId}
import java.util.UUID
import java.util.concurrent.TimeUnit

import scala.concurrent.duration.FiniteDuration

package object common {
  def utc: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC"))

  def uuid: UUID = UUID.randomUUID()

  implicit def localDateTimeOrdering: Ordering[LocalDateTime] = Ordering.fromLessThan[LocalDateTime](_ isBefore _)

  implicit class LocalDateTimeOps(value: LocalDateTime) {
    def >(operand: LocalDateTime): Boolean = value isAfter operand
    def <(operand: LocalDateTime): Boolean = value isBefore operand
    def >=(operand: LocalDateTime): Boolean = !value.isBefore(operand)
    def <=(operand: LocalDateTime): Boolean = !value.isAfter(operand)
    def +(operand: FiniteDuration): LocalDateTime = value plusNanos operand.toNanos
    def -(operand: FiniteDuration): LocalDateTime = value minusNanos operand.toNanos
    def -(operand: LocalDateTime): FiniteDuration = FiniteDuration(value.until(operand, ChronoUnit.NANOS), TimeUnit.NANOSECONDS)
  }
}
