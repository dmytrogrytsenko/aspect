package aspect

import java.time.LocalDateTime

import aspect.common._

import scala.util.Random

trait EntityGenerators {
  lazy val random = new Random()
  def genInt(bound: Int): Int = random.nextInt(bound)
  def genBoolean: Boolean = random.nextBoolean()
  def genString: String = uuid.toString
  def genLocalDateTime: LocalDateTime = LocalDateTime.now.minusMinutes(genInt(60))
  def genOption[T](value: => T): Option[T] = if (genBoolean) Some(value) else None
}
