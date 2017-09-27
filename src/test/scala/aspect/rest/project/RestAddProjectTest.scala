package aspect.rest.project

import aspect.TestBase
import aspect.common._
import aspect.domain.{Project, SessionToken, UserId}
import aspect.rest.RestErrors.{BadRequest, Unauthorized}
import aspect.rest.models.{AddProjectData, AddProjectResult}

import scala.concurrent.duration._

class RestAddProjectTest extends TestBase {

  behavior of "POST /projects"

  it should "create new project" in {
    //arrange
    val user = Mongo.addUser()
    val session = Mongo.addSession(userId = user.id)
    val data = AddProjectData(name = genString)
    //act
    val result = Rest.addProject(data, session.token).to[AddProjectResult]
    //assert
    val storedProject = Mongo.getProject(result.projectId).value
    storedProject shouldBe Project(result.projectId, user.id, data.name, storedProject.createdAt)
    storedProject.createdAt shouldBeInRange  utc +- 2.seconds
    //cleanup
    Mongo.removeUser(user.id)
    Mongo.removeSession(session.token)
    Mongo.removeProject(result.projectId)
  }

  it should "return 400 (Bad Request) VALIDATION if name is empty" in {
    //arrange
    val user = Mongo.addUser()
    val session = Mongo.addSession(userId = user.id)
    //act
    val result = Rest.addProject(AddProjectData(""), session.token).toErrorResult
    //assert
    result shouldBe BadRequest.Validation.requiredMemberEmpty("name")
    //cleanup
    Mongo.removeUser(user.id)
    Mongo.removeSession(session.token)
  }

  it should "return 401 (Unauthorized) CREDENTIALS_MISSING if authorization token is not defined" in {
    //act
    val result = Rest.addProject(AddProjectData(genString)).toErrorResult
    //assert
    result shouldBe Unauthorized.credentialsMissing
  }

  it should "return 401 (Unauthorized) CREDENTIALS_REJECTED if authorization token is not correct" in {
    //act
    val result = Rest.addProject(AddProjectData(genString), SessionToken.gen).toErrorResult
    //assert
    result shouldBe Unauthorized.credentialsRejected
  }

  it should "return 401 (Unauthorized) CREDENTIALS_REJECTED if user not found" in {
    //arrange
    val userId = UserId.gen
    val session = Mongo.addSession(userId = userId)
    val data = AddProjectData(name = genString)
    //act
    val result = Rest.addProject(data, session.token).toErrorResult
    //assert
    result shouldBe Unauthorized.credentialsRejected
    //cleanup
    Mongo.removeSession(session.token)
  }

}
