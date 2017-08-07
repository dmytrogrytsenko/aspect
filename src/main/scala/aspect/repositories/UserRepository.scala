package aspect.repositories

import aspect.common.actors.{BaseActor, NodeSingleton}
import aspect.domain.{User, UserId}

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

class UserRepository extends BaseActor {
  import UserRepository._

  def receive: Receive = working(Nil)

  def working(users: List[User]): Receive = {
    case FindUserById(userId) =>
      val result = users.find(_.id == userId) match {
        case Some(user) => UserFoundById(user)
        case None => UserNotFoundById(userId)
      }
     sender ! result

    case FindUserByName(username) =>
      val result = users.find(_.name == username) match {
        case Some(user) => UserFoundByName(user)
        case None => UserNotFoundByName(username)
      }
      sender ! result
  }
}
