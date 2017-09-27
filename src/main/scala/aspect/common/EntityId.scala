package aspect.common

import aspect.common.Crypt._
import aspect.common.Shard._

import scala.reflect.ClassTag

trait EntityId {
  def value: String
  lazy val adler32: Adler32 = value.adler32
  override def hashCode: Int = adler32.underlying
  override def toString: String = s"${getClass.getSimpleName}($value,$shard)"
  def shard: Shard = hashCode.toShard
}

trait EntityIdCompanion[T <: EntityId] {
  def apply(value: String)(implicit tag: ClassTag[T]): T =
    tag.runtimeClass.getConstructor(classOf[String]).newInstance(value).asInstanceOf[T]

  def gen(implicit tag: ClassTag[T]): T = apply(uuid.toString)
}
