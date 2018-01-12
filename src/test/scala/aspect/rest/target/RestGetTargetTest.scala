package aspect.rest.target

import aspect.TestBase
import aspect.domain.{ProjectId, SessionToken, TargetId, UserId}
import aspect.rest.RestErrors.{Forbidden, NotFound, Unauthorized}
import aspect.rest.models.{TargetProjectResult, TargetResult}

class RestGetTargetTest extends TestBase {

  behavior of "GET /targets/:id"

  it should "return target successfully" in {
    //arrange
    val user = Mongo.addUser()
    val session = Mongo.addSession(userId = user.id)
    val project = Mongo.addProject(userId = user.id)
    val target = Mongo.addTarget(projectId = project.id)
    //act
    val result = Rest.getTarget(target.id, session.token).to[TargetResult]
    //assert
    result shouldBe TargetResult(target, project, user)
    //cleanup
    Mongo.removeUser(user.id)
    Mongo.removeSession(session.token)
    Mongo.removeProject(project.id)
    Mongo.removeTarget(target.id)
  }

  it should "return 401 (Unauthorized) CREDENTIALS_MISSING if authorization token is not defined" in {
    //act
    val result = Rest.getTarget(TargetId.gen).toErrorResult
    //assert
    result shouldBe Unauthorized.credentialsMissing
  }

  it should "return 401 (Unauthorized) CREDENTIALS_REJECTED if authorization token is not correct" in {
    //act
    val result = Rest.getTarget(TargetId.gen, SessionToken.gen).toErrorResult
    //assert
    result shouldBe Unauthorized.credentialsRejected
  }

  it should "return 401 (Unauthorized) CREDENTIALS_REJECTED if user not found" in {
    //arrange
    val userId = UserId.gen
    val session = Mongo.addSession(userId = userId)
    val project = Mongo.addProject(userId = userId)
    val target = Mongo.addTarget(projectId = project.id)
    //act
    val result = Rest.getTarget(target.id, session.token).toErrorResult
    //assert
    result shouldBe Unauthorized.credentialsRejected
    //cleanup
    Mongo.removeSession(session.token)
    Mongo.removeProject(project.id)
    Mongo.removeTarget(target.id)
  }

  it should "return 404 (Not Found) TARGET_NOT_FOUND if target not found" in {
    //arrange
    val targetId = TargetId.gen
    val user = Mongo.addUser()
    val session = Mongo.addSession(userId = user.id)
    //act
    val result = Rest.getTarget(targetId, session.token).toErrorResult
    //assert
    result shouldBe NotFound.targetNotFound
    //cleanup
    Mongo.removeUser(user.id)
    Mongo.removeSession(session.token)
  }

  it should "return 404 (Not Found) PROJECT_NOT_FOUND if project not found" in {
    //arrange
    val projectId = ProjectId.gen
    val user = Mongo.addUser()
    val session = Mongo.addSession(userId = user.id)
    val target = Mongo.addTarget(projectId = projectId)
    //act
    val result = Rest.getTarget(target.id, session.token).toErrorResult
    //assert
    result shouldBe NotFound.projectNotFound
    //cleanup
    Mongo.removeUser(user.id)
    Mongo.removeSession(session.token)
    Mongo.removeTarget(target.id)
  }

  it should "return 401 (Unauthorized) ACCESS_DENIED if user is not owner of project" in {
    //arrange
    val owner = Mongo.addUser()
    val project = Mongo.addProject(userId = owner.id)
    val target = Mongo.addTarget(projectId = project.id)
    val user = Mongo.addUser()
    val session = Mongo.addSession(userId = user.id)
    //act
    val result = Rest.getTarget(target.id, session.token).toErrorResult
    //assert
    result shouldBe Forbidden.accessDenied
    //cleanup
    Mongo.removeUser(owner.id)
    Mongo.removeUser(user.id)
    Mongo.removeSession(session.token)
    Mongo.removeProject(project.id)
    Mongo.removeTarget(target.id)
  }
}