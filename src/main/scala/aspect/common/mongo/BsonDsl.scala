package aspect.common.mongo

import java.time.{LocalDateTime, ZoneOffset}
import java.util.Date

import aspect.common.Crypt._
import reactivemongo.bson._

import scala.reflect.ClassTag

object BsonDsl extends BsonDsl

trait BsonDsl {

  implicit class BSONDocumentOps(doc: BSONDocument) {
    def asOpt[T : ClassTag](key: String)(implicit reader: BSONReader[_ <: BSONValue, T]): Option[T] = {
      try {
        doc.getAs[T](key)
      } catch {
        case e: Throwable =>
          throw new IllegalArgumentException(
            s"Parse BSONDocument exception. " +
            s"Key: $key. Type: ${implicitly[ClassTag[T]].runtimeClass.getSimpleName}", e)
      }
    }

    def as[T : ClassTag](key: String)(implicit reader: BSONReader[_ <: BSONValue, T]): T =
      doc.asOpt[T](key).getOrElse {
        throw new NoSuchElementException(s"No key $key in BSONDocument")
      }
  }

  implicit val localDateTimeHandler: BSONHandler[BSONDateTime, LocalDateTime] = BSONHandler(
    bson => new Date(bson.value).toInstant.atOffset(ZoneOffset.UTC).toLocalDateTime,
    value => BSONDateTime(value.toInstant(ZoneOffset.UTC).toEpochMilli)
  )

  implicit val sha256Handler: BSONHandler[BSONString, Sha256] = BSONHandler(
    bson => Sha256(bson.value),
    value => BSONString(value.underlying)
  )

  def $empty: BSONDocument = BSONDocument.empty
  def $doc(elements: Producer[BSONElement]*): BSONDocument = BSONDocument(elements: _*)
  def $id[T](id: T)(implicit writer: BSONWriter[T, _ <: BSONValue]): BSONDocument = $doc("_id" -> id)
  def $asc(field: String): BSONDocument = $doc(field -> 1)
  def $desc(field: String): BSONDocument = $doc(field -> -1)
  def $set(item: Producer[BSONElement], items: Producer[BSONElement]*): BSONDocument =
    $doc("$set" -> $doc(Seq(item) ++ items: _*))
}
