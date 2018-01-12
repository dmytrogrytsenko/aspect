package aspect.rest.target

import aspect.TestBase
import aspect.common._
import aspect.domain.{ProjectId, SessionToken, Target}
import aspect.rest.RestErrors.{BadRequest, Forbidden, NotFound, Unauthorized}
import aspect.rest.models.{AddTargetData, AddTargetResult}

import scala.concurrent.duration._

class RestAddTargetTest extends TestBase {
  behavior of "POST /targets"

  it should "create new target" in {
    //arrange
    val user = Mongo.addUser()
    val session = Mongo.addSession(userId = user.id)
    val project = Mongo.addProject(userId = user.id)
    val data = AddTargetData(name = genString, projectId = project.id, keywords = genString)
    //act
    val result = Rest.addTarget(data, session.token).to[AddTargetResult]
    //assert
    val storedTarget = Mongo.getTarget(result.targetId).value
    storedTarget shouldBe Target(result.targetId, project.id, data.name, data.keywords, storedTarget.createdAt)
    storedTarget.createdAt shouldBeInRange  utc +- 2.seconds
    //cleanup
    Mongo.removeUser(user.id)
    Mongo.removeSession(session.token)
    Mongo.removeProject(project.id)
    Mongo.removeTarget(result.targetId)
  }

  it should "return 400 (Bad Request) VALIDATION if name is empty" in {
    //arrange
    val user = Mongo.addUser()
    val session = Mongo.addSession(userId = user.id)
    val project = Mongo.addProject(userId = user.id)
    val data = AddTargetData(project.id, "", genString)
    //act
    val result = Rest.addTarget(data, session.token).toErrorResult
    //assert
    result shouldBe BadRequest.Validation.requiredMemberEmpty("name")
    //cleanup
    Mongo.removeUser(user.id)
    Mongo.removeSession(session.token)
    Mongo.removeProject(project.id)
  }

  it should "return 401 (Unauthorized) CREDENTIALS_MISSING if authorization token is not defined" in {
    //arrange
    val data = AddTargetData(ProjectId.gen, genString, genString)
    //act
    val result = Rest.addTarget(data, null).toErrorResult
    //assert
    result shouldBe Unauthorized.credentialsMissing
  }

  it should "return 401 (Unauthorized) CREDENTIALS_REJECTED if authorization token is not correct" in {
    //arrange
    val data = AddTargetData(ProjectId.gen, genString, genString)
    //act
    val result = Rest.addTarget(data, SessionToken.gen).toErrorResult
    //assert
    result shouldBe Unauthorized.credentialsRejected
  }

  it should "return 401 (Unauthorized) CREDENTIALS_REJECTED if user not found" in {
    //arrange
    val session = Mongo.addSession()
    val project = Mongo.addProject()
    val data = AddTargetData(project.id, genString, genString)
    //act
    val result = Rest.addTarget(data, session.token).toErrorResult
    //assert
    result shouldBe Unauthorized.credentialsRejected
    //cleanup
    Mongo.removeSession(session.token)
    Mongo.removeProject(project.id)
  }

  it should "return 401 (Unauthorized) ACCESS_DENIED if user is not owner of project" in {
    //arrange
    val owner = Mongo.addUser()
    val project = Mongo.addProject(userId = owner.id)
    val user = Mongo.addUser()
    val session = Mongo.addSession(userId = user.id)
    val data = AddTargetData(project.id, genString, genString)
    //act
    val result = Rest.addTarget(data, session.token).toErrorResult
    //assert
    result shouldBe Forbidden.accessDenied
    //cleanup
    Mongo.removeUser(owner.id)
    Mongo.removeUser(user.id)
    Mongo.removeSession(session.token)
    Mongo.removeProject(project.id)
  }

  it should "return 404 (Not Found) PROJECT_NOT_FOUND if project not found" in {
    //arrange
    val projectId = ProjectId.gen
    val user = Mongo.addUser()
    val session = Mongo.addSession(userId = user.id)
    val data = AddTargetData(projectId, genString, genString)
    //act
    val result = Rest.addTarget(data, session.token).toErrorResult
    //assert
    result shouldBe NotFound.projectNotFound
    //cleanup
    Mongo.removeUser(user.id)
    Mongo.removeSession(session.token)
  }
}
