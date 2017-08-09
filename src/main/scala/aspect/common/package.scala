package aspect

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID
import java.util.concurrent.TimeUnit

import scala.concurrent.duration.FiniteDuration

package object common {
  def now: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC"))

  def uuid: UUID = UUID.randomUUID()

  implicit class PipedObject[T](value: T) {
    def ~>[R](f: T => R): R = f(this.value)
    def pipe[R](f: T => R): R = f(this.value)
  }

  implicit class PipedFunc[T, R](f: T => R) {
    def <~[Z](v: Z => T): Z => R = x => f(v(x))
    def <~(v: T): R = f(v)
  }

  implicit class RichDateTime(value: LocalDateTime) {
    def >(operand: LocalDateTime): Boolean = value isAfter operand
    def <(operand: LocalDateTime): Boolean = value isBefore operand
    def >=(operand: LocalDateTime): Boolean = !value.isBefore(operand)
    def <=(operand: LocalDateTime): Boolean = !value.isAfter(operand)
    def +(operand: FiniteDuration): LocalDateTime = value plusNanos operand.toNanos
    def -(operand: FiniteDuration): LocalDateTime = value plusNanos operand.toNanos
    def -(operand: LocalDateTime): FiniteDuration = FiniteDuration(value.getNano - operand.getNano, TimeUnit.NANOSECONDS)
  }
}
