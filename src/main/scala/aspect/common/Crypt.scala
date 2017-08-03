package aspect.common

import java.security.MessageDigest

object Crypt {

  case class Md5(underlying: Array[Byte]) {
    override def toString: String = s"${getClass.getSimpleName}(${underlying.HEX})"
  }

  case class Sha256(underlying: Array[Byte]) {
    override def toString: String = s"${getClass.getSimpleName}(${underlying.HEX})"
  }

  case class Adler32(underlying: Int) {
    override def toString: String = s"${getClass.getSimpleName}(${underlying.HEX})"
  }

  val md5Algorithm: MessageDigest = MessageDigest.getInstance("MD5")
  val sha256Algorithm: MessageDigest = MessageDigest.getInstance("SHA-256")

  def md5hash(bytes: Array[Byte]): Array[Byte] = md5Algorithm.digest(bytes)
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
    def HEX: String = value.hex.toUpperCase
    def hex: String = Integer.toHexString(value)
  }

  implicit class ArrayOfBytesCryptOps(val bytes: Array[Byte]) extends AnyVal {
    def HEX: String = bytes.map("%02X".format(_)).mkString
    def hex: String = bytes.map("%02x".format(_)).mkString
    def md5: Md5 = Md5(md5hash(bytes))
    def sha256: Sha256 = Sha256(sha256hash(bytes))
    def adler32: Adler32 = Adler32(adler32sum(bytes))
  }

  implicit class StringCryptOps(val value: String) extends AnyVal {
    def HEX: String = value.getBytes.HEX
    def hex: String = value.getBytes.hex
    def md5: Md5 = value.getBytes.md5
    def sha256: Sha256 = value.getBytes.sha256
    def adler32: Adler32 = value.getBytes.adler32
  }
}
