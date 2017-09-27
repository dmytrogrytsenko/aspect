package aspect.repositories

import akka.pattern.pipe
import aspect.common.Messages.Start
import aspect.common.actors.{BaseActor, NodeSingleton}
import aspect.common.mongo.MongoDatabase
import aspect.domain.{User, UserId}
import aspect.mongo.{BsonProtocol, UserCollection}

object UserRepository extends NodeSingleton[UserRepository] {
  case class FindUserById(userId: UserId)
  sealed trait FindUserByIdResult
  case class UserFoundById(user: User) extends FindUserByIdResult
  case class UserNotFoundById(userId: UserId) extends FindUserByIdResult

  case class FindUserByName(username: String)
  sealed trait FindUserByNameResult
  case class UserFoundByName(user: User) extends FindUserByNameResult
  case class UserNotFoundByName(username: String) extends FindUserByNameResult
}

class UserRepository extends BaseActor with BsonProtocol {
  import UserRepository._
  import context.dispatcher

  val collection = new UserCollection(MongoDatabase.db)

  def receive: Receive = {
    case Start => collection.ensureIndexes

    case FindUserById(userId) =>
      collection.get(userId).map {
        case Some(user) => UserFoundById(user)
        case None => UserNotFoundById(userId)
      } pipeTo sender

    case FindUserByName(username) =>
      collection.findUserByName(username).map {
        case Some(user) => UserFoundByName(user)
        case None => UserNotFoundByName(username)
      } pipeTo sender
  }
}
