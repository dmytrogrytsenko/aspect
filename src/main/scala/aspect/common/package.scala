package aspect

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

package object common {
  def now: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC"))

  def uuid: UUID = UUID.randomUUID()

  implicit class PipedObject[T](value: T) {
    def ~>[R](f: T => R): R = f(this.value)
    def pipe[R](f: T => R): R = f(this.value)
  }
}
