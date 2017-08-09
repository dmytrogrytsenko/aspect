package aspect.entities

import aspect.common.Crypt._
import aspect.domain.{User, UserId}
import scalikejdbc._
import scalikejdbc.async._
import scalikejdbc.async.FutureImplicits._

import scala.concurrent.Future

trait Table {
  implicit val userIdBinder: Binders[UserId] = Binders.string.xmap(UserId.apply, _.value)
  implicit val sha256Binder: Binders[Sha256] = Binders.string.xmap(Sha256.parse, _.underlying.hex)
}

object UserTable extends Table with SQLSyntaxSupport[User] {

  private val u = UserTable.syntax

  private def toUser(u: SyntaxProvider[User])(rs: WrappedResultSet): User = autoConstruct(rs, u)
  //def toUser(u: ResultName[User])(rs: WrappedResultSet): User = autoConstruct(rs, u)

  def get(id: UserId)(implicit session: AsyncDBSession): Future[Option[User]] =
    withSQL {
      select.from(UserTable as u).where.eq(u.id, id)
    } map toUser(u)

  def all(implicit session: AsyncDBSession): Future[List[User]] =
    withSQL {
      select.from(UserTable as u)
    } map toUser(u)

  def findByName(name: String)(implicit session: AsyncDBSession): Future[Option[User]] =
    withSQL {
      select.from(UserTable as u).where.eq(u.name, name)
    } map toUser(u)
}
