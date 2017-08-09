package aspect.entities

import aspect.domain.User
import scalikejdbc._

case class UserRow()

object UserTable extends SQLSyntaxSupport[UserRow] {

}
