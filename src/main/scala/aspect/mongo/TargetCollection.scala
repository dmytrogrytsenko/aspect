package aspect.mongo

import akka.http.scaladsl.util.FastFuture
import aspect.common.mongo.MongoCollection
import aspect.domain.{ProjectId, Target, TargetId}
import reactivemongo.api.{Cursor, DB, ReadPreference}
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson._

import scala.concurrent.{ExecutionContext, Future}

class TargetCollection(val db: DB) extends MongoCollection[TargetId, Target] with BsonProtocol {

  override val name = "targets"

  override lazy val indexes = List(Index(Seq("projectId" -> IndexType.Ascending)))

  def getProjectTargets(projectId: ProjectId)
                       (implicit ec: ExecutionContext): Future[List[Target]] =
    items
      .find($doc("projectId" -> projectId))
      .cursor[Target](ReadPreference.nearest)
      .collect[List](-1, Cursor.FailOnError[List[Target]]())

  def update(targetId: TargetId,
             name: Option[String] = None,
             keywords: Option[String] = None)
            (implicit ec: ExecutionContext): Future[Unit] =
    if (name.isEmpty && keywords.isEmpty) FastFuture.successful[Unit](())
    else items.update($id(targetId), $set("name" -> name, "keywords" -> keywords)).map(_ => {})
}
