package aspect.mongo

import akka.http.scaladsl.util.FastFuture
import aspect.common.mongo.MongoCollection
import aspect.domain.{Project, ProjectId, UserId}
import reactivemongo.api.{Cursor, DB, ReadPreference}
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson._

import scala.concurrent.{ExecutionContext, Future}

class ProjectCollection(val db: DB) extends MongoCollection[ProjectId, Project] with BsonProtocol {

  override val name = "projects"

  override lazy val indexes = List(
    Index(Seq("userId" -> IndexType.Ascending)))

  def getUserProjects(userId: UserId)
                     (implicit ec: ExecutionContext): Future[List[Project]] = {
    items
      .find($doc("userId" -> userId))
      .cursor[Project](ReadPreference.nearest)
      .collect[List](-1, Cursor.FailOnError[List[Project]]())
  }

  def update(projectId: ProjectId, name: Option[String])
            (implicit ec: ExecutionContext): Future[Unit] =
    if (name.isEmpty) FastFuture.successful[Unit](())
    else items.update($id(projectId), $set("name" -> name)).map(_ => { })
}
