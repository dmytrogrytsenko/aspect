package aspect.common

import java.security.MessageDigest

object Crypt {

  case class Sha256 private(underlying: String) extends AnyVal {
    override def toString: String = underlying
  }

  object Sha256 {
    def apply(hash: Array[Byte]): Sha256 = new Sha256(hash.hex)
    def apply(hash: String): Sha256 = new Sha256(hash.toLowerCase)
    def eval(data: Array[Byte]): Sha256 = Sha256(sha256hash(data))
    def eval(data: String): Sha256 = Sha256(sha256hash(data.getBytes))
  }

  case class Adler32(underlying: Int) extends AnyVal {
    override def toString: String = underlying.hex
  }

  object Adler32 {
    def eval(data: Array[Byte]): Adler32 = Adler32(adler32sum(data))
    def eval(data: String): Adler32 = Adler32(adler32sum(data.getBytes))
  }

  def hex2bytes(hex: String): Array[Byte] =
    hex.sliding(2, 2).map(Integer.parseInt(_, 16).toByte).toArray

  val sha256Algorithm: MessageDigest = MessageDigest.getInstance("SHA-256")

  def sha256hash(bytes: Array[Byte]): Array[Byte] = sha256Algorithm.digest(bytes)

  def adler32sum(bytes: Array[Byte]): Int = {
    var a = 1
    var b = 0
    bytes.foreach { char =>
      a = (char + a) % 65521
      b = (b + a) % 65521
    }
    (b << 16) + a
  }

  implicit class IntegerCryptOps(val value: Int) extends AnyVal {
    def hex: String = Integer.toHexString(value)
  }

  implicit class ArrayOfBytesCryptOps(val bytes: Array[Byte]) extends AnyVal {
    def hex: String = bytes.map("%02x".format(_)).mkString
    def sha256: Sha256 = Sha256.eval(bytes)
    def adler32: Adler32 = Adler32.eval(bytes)
  }

  implicit class StringCryptOps(val value: String) extends AnyVal {
    def hex: String = value.getBytes.hex
    def sha256: Sha256 = Sha256.eval(value)
    def adler32: Adler32 = Adler32.eval(value)
  }
}
