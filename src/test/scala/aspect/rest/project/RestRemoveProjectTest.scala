package aspect.rest.project

import aspect.TestBase
import aspect.common._
import aspect.domain.{ProjectId, SessionToken, UserId}
import aspect.rest.RestErrors.{Forbidden, NotFound, Unauthorized}

class RestRemoveProjectTest extends TestBase {

  behavior of "DELETE /projects/:id"

  it should "delete project" in {
    //arrange
    val user = Mongo.addUser()
    val session = Mongo.addSession(userId = user.id)
    val project = Mongo.addProject(userId = user.id)
    //act
    Rest.removeProject(project.id, session.token).shouldBeOK()
    //assert
    Mongo.getProject(project.id) shouldBe None
    //cleanup
    Mongo.removeUser(user.id)
    Mongo.removeSession(session.token)
  }

  it should "return 401 (Unauthorized) CREDENTIALS_MISSING if authorization token is not defined" in {
    //act
    val result = Rest.removeProject(ProjectId.gen).toErrorResult
    //assert
    result shouldBe Unauthorized.credentialsMissing
  }

  it should "return 401 (Unauthorized) CREDENTIALS_REJECTED if authorization token is not correct" in {
    //act
    val result = Rest.removeProject(ProjectId.gen, SessionToken.gen).toErrorResult
    //assert
    result shouldBe Unauthorized.credentialsRejected
  }

  it should "return 401 (Unauthorized) CREDENTIALS_REJECTED if user not found" in {
    //arrange
    val userId = UserId.gen
    val session = Mongo.addSession(userId = userId)
    val project = Mongo.addProject(userId = userId)
    //act
    val result = Rest.removeProject(project.id, session.token).toErrorResult
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
    //act
    val result = Rest.removeProject(project.id, session.token).toErrorResult
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
    //act
    val result = Rest.removeProject(projectId, session.token).toErrorResult
    //assert
    result shouldBe NotFound.projectNotFound
    //cleanup
    Mongo.removeUser(user.id)
    Mongo.removeSession(session.token)
  }
}
