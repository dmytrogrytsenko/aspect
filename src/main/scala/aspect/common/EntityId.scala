package aspect.common

import aspect.common.Crypt._
import aspect.common.Shard._

trait EntityId {
  def value: String
  lazy val adler32: Adler32 = value.adler32
  override def hashCode: Int = adler32.underlying
  override def toString: String = s"${getClass.getSimpleName}($value,SH$shard)"
  def shard: Shard = hashCode.toShard
}
