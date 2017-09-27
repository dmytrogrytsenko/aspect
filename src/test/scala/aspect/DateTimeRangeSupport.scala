package aspect

import java.time.LocalDateTime

import aspect.common._
import org.scalatest.{Assertion, Matchers}

import scala.concurrent.duration.FiniteDuration

trait DateTimeRangeSupport extends Matchers {
  implicit class DateTimeRangeBuilder(instance: LocalDateTime) {
    def +-(tolerance: FiniteDuration): (LocalDateTime, LocalDateTime) =
      (instance - tolerance) -> (instance + tolerance)
  }

  implicit class DateTimeRangeMatcher(instance: LocalDateTime) {
    def shouldBeInRange(range: (LocalDateTime, LocalDateTime)): Assertion = {
      instance should be >= range._1
      instance should be <= range._2
    }
  }
}
