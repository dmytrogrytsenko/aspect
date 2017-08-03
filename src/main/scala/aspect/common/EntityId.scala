package aspect.common

import aspect.common.Crypt._
import aspect.common.Shard._

trait EntityId {
  def value: String
  def sha256: Sha256
  def adler32: Adler32
  def shard: Shard = adler32.underlying.toShard

  override def hashCode: Int = adler32.underlying

  override def equals(obj: Any): Boolean = obj match {
    case id: EntityId => id.adler32 == this.adler32 && id.value == this.value
    case _ => false
  }

  override def toString: String = s"${getClass.getSimpleName}($value,$sha256,$adler32,$shard)"
}

trait EntityIdCompanion[T <: EntityId] {
  def apply(value: String, sha256: Sha256, adler32: Adler32): T = new T {
    val value: String = value
    val sha256: Sha256 = sha256
    val adler32: Adler32 = adler32
  }

  def apply(value: String): T = apply(value, value.sha256, value.adler32)
}

