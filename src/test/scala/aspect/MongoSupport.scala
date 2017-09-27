package aspect

import java.time.LocalDateTime

import aspect.common.Crypt.Sha256
import aspect.common._
import aspect.common.extensions.FutureExtensions._
import aspect.common.Crypt._
import aspect.domain._
import aspect.mongo._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

trait MongoSupport extends EntityGenerators with BsonProtocol {

  object Mongo {

    implicit val timeout: Duration = 5.seconds

    import aspect.common.mongo.MongoDatabase.db
    lazy val users = new UserCollection(db)
    lazy val sessions = new SessionCollection(db)
    lazy val projects = new ProjectCollection(db)
    lazy val targets = new TargetCollection(db)

    def addUser(id: UserId = UserId.gen,
                name: String = genString,
                password: String = genString,
                email: String = genString,
                firstName: Option[String] = genOption(genString),
                lastName: Option[String] = genOption(genString),
                createdAt: LocalDateTime = utc): User = {
      val user = User(id, name, password, email, firstName, lastName, createdAt)
      users.add(user).await
      user
    }

    def removeUser(id: UserId): Unit = users.remove(id).await

    def addSession(token: SessionToken = SessionToken.gen,
                   userId: UserId = UserId.gen,
                   createdAt: LocalDateTime = utc,
                   lastActivityAt: LocalDateTime = utc): Session = {
      val session = Session(token, userId, createdAt, lastActivityAt)
      sessions.add(session).await
      session
    }

    def removeSession(token: SessionToken): Unit = sessions.remove(token).await

    def getSession(token: SessionToken): Option[Session] = sessions.get(token).await

    def getProject(id: ProjectId): Option[Project] = projects.get(id).await

    def addProject(id: ProjectId = ProjectId.gen,
                   userId: UserId = UserId.gen,
                   name: String = genString,
                   createdAt: LocalDateTime = utc): Project = {
      val project = Project(id, userId, name, createdAt)
      projects.add(project).await
      project
    }

    def removeProject(id: ProjectId): Unit = projects.remove(id).await

    def getTarget(id: TargetId): Option[Target] = targets.get(id).await

    def addTarget(id: TargetId = TargetId.gen,
                  projectId: ProjectId = ProjectId.gen,
                  name: String = genString,
                  keywords: String = genString,
                  createdAt: LocalDateTime = utc): Target = {
      val target = Target(id, projectId, name, keywords, createdAt)
      targets.add(target).await
      target
    }

    def removeTarget(id: TargetId): Unit = targets.remove(id).await
  }
}

