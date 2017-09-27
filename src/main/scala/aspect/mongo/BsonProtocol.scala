package aspect.mongo

import java.time.LocalDateTime

import aspect.common.mongo.BsonDsl
import aspect.domain._
import reactivemongo.bson._

object BsonProtocol extends BsonProtocol

trait BsonProtocol extends BsonDsl {

  implicit val userIdHandler: BSONHandler[BSONString, UserId] =
    BSONHandler(bson => UserId(bson.value), id => BSONString(id.value))

  implicit val projectIdHandler: BSONHandler[BSONString, ProjectId] =
    BSONHandler(bson => ProjectId(bson.value), id => BSONString(id.value))

  implicit val sessionTokenHandler: BSONHandler[BSONString, SessionToken] =
    BSONHandler(bson => SessionToken(bson.value), id => BSONString(id.value))

  implicit val targetIdHandler: BSONHandler[BSONString, TargetId] =
    BSONHandler(bson => TargetId(bson.value), id => BSONString(id.value))

  implicit object UserBSONDocumentReader extends BSONDocumentReader[User] {
    def read(doc: BSONDocument): User =
      User(
        id = doc.as[UserId]("_id"),
        name = doc.as[String]("name"),
        password = doc.as[String]("password"),
        email = doc.as[String]("email"),
        firstName = doc.asOpt[String]("firstName"),
        lastName = doc.asOpt[String]("lastName"),
        createdAt = doc.as[LocalDateTime]("createdAt"))
  }

  implicit object UserBSONDocumentWriter extends BSONDocumentWriter[User] {
    def write(value: User): BSONDocument = $doc(
      "_id" -> value.id,
      "name" -> value.name,
      "nameLC" -> value.name.toLowerCase,
      "password" -> value.password,
      "email" -> value.email,
      "firstName" -> value.firstName,
      "lastName" -> value.lastName,
      "createdAt" -> value.createdAt)
  }

  implicit object SessionBSONDocumentReader extends BSONDocumentReader[Session] {
    def read(doc: BSONDocument): Session =
      Session(
        token = doc.as[SessionToken]("_id"),
        userId = doc.as[UserId]("userId"),
        createdAt = doc.as[LocalDateTime]("createdAt"),
        lastActivityAt = doc.as[LocalDateTime]("lastActivityAt"))
  }

  implicit object SessionBSONDocumentWriter extends BSONDocumentWriter[Session] {
    def write(value: Session): BSONDocument = $doc(
      "_id" -> value.token,
      "userId" -> value.userId,
      "createdAt" -> value.createdAt,
      "lastActivityAt" -> value.lastActivityAt)
  }

  implicit object ProjectReader extends BSONDocumentReader[Project] {
    def read(doc: BSONDocument) = Project(
      id = doc.as[ProjectId]("_id"),
      userId = doc.as[UserId]("userId"),
      name = doc.as[String]("name"),
      createdAt = doc.as[LocalDateTime]("createdAt"))
  }

  implicit object ProjectWriter extends BSONDocumentWriter[Project] {
    def write(value: Project): BSONDocument = $doc(
      "_id" -> value.id,
      "userId" -> value.userId,
      "name" -> value.name,
      "createdAt" -> value.createdAt)
  }

  implicit object TargetReader extends BSONDocumentReader[Target] {
    def read(doc: BSONDocument) = Target(
      id = doc.as[TargetId]("_id"),
      projectId = doc.as[ProjectId]("projectId"),
      name = doc.as[String]("name"),
      keywords = doc.as[String]("keywords"),
      createdAt = doc.as[LocalDateTime]("createdAt"))
  }

  implicit object TargetWriter extends BSONDocumentWriter[Target] {
    def write(value: Target): BSONDocument = $doc(
      "_id" -> value.id,
      "projectId" -> value.projectId,
      "name" -> value.name,
      "keywords" -> value.keywords,
      "createdAt" -> value.createdAt)
  }

}
