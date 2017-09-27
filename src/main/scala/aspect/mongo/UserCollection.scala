package aspect.mongo

import aspect.common.mongo.MongoCollection
import aspect.domain.{User, UserId}
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.api.DB
import reactivemongo.bson._

import scala.concurrent.{ExecutionContext, Future}

class UserCollection(val db: DB) extends MongoCollection[UserId, User] with BsonProtocol {

  override val name = "users"

  override lazy val indexes = List(
    Index(Seq("nameLC" -> IndexType.Ascending), unique = true))

  def findUserByName(username: String)
                    (implicit ec: ExecutionContext): Future[Option[User]] =
    items.find($doc("nameLC" -> username.toLowerCase)).one[User]
}
