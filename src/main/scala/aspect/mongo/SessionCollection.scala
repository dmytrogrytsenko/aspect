package aspect.mongo

import aspect.common._
import aspect.common.mongo.MongoCollection
import aspect.domain.{Session, SessionToken}
import reactivemongo.api.DB
import reactivemongo.api.indexes.{Index, IndexType}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class SessionCollection(val db: DB) extends MongoCollection[SessionToken, Session] with BsonProtocol {

  override val name = "sessions"

  override lazy val indexes = List(
    Index(Seq("lastActivityAt" -> IndexType.Descending), options = $doc("expireAfterSeconds" -> 14.days.toSeconds)))

  def activity(token: SessionToken)
              (implicit ec: ExecutionContext): Future[Unit] =
    items
      .update($id(token), $set("lastActivityAt" -> utc))
      .map(_ => { })
      .recover { case e => println(e) }
}
