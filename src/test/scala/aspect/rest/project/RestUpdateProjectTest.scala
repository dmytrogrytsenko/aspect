package aspect.rest.project

import aspect.TestBase
import aspect.domain.{ProjectId, SessionToken, UserId}
import aspect.rest.RestErrors.{BadRequest, Forbidden, NotFound, Unauthorized}
import aspect.rest.models.UpdateProjectData

class RestUpdateProjectTest extends TestBase {

  behavior of "PUT /projects/:id"

  it should "do nothing if no fileds defined" in {
    //arrange
    val user = Mongo.addUser()
    val session = Mongo.addSession(userId = user.id)
    val project = Mongo.addProject(userId = user.id)
    val data = UpdateProjectData()
    //act
    Rest.updateProject(project.id, data, session.token).shouldBeOK()
    //assert
    Mongo.getProject(project.id).value shouldBe project
    //cleanup
    Mongo.removeUser(user.id)
    Mongo.removeSession(session.token)
    Mongo.removeProject(project.id)
  }

  it should "update name only" in {
    //arrange
    val user = Mongo.addUser()
    val session = Mongo.addSession(userId = user.id)
    val project = Mongo.addProject(userId = user.id)
    val data = UpdateProjectData(name = Some(genString))
    //act
    Rest.updateProject(project.id, data, session.token).shouldBeOK()
    //assert
    Mongo.getProject(project.id).value shouldBe project.copy(name = data.name.get)
    //cleanup
    Mongo.removeUser(user.id)
    Mongo.removeSession(session.token)
    Mongo.removeProject(project.id)
  }

  it should "return 400 (Bad Request) VALIDATION if name is empty" in {
    //arrange
    val user = Mongo.addUser()
    val session = Mongo.addSession(userId = user.id)
    val project = Mongo.addProject(userId = user.id)
    val data = UpdateProjectData(Some(""))
    //act
    val result = Rest.updateProject(project.id, data, session.token).toErrorResult
    //assert
    result shouldBe BadRequest.Validation.requiredMemberEmpty("name")
    //cleanup
    Mongo.removeUser(user.id)
    Mongo.removeSession(session.token)
    Mongo.removeProject(project.id)
  }

  it should "return 401 (Unauthorized) CREDENTIALS_MISSING if authorization token is not defined" in {
    //act
    val result = Rest.updateProject(ProjectId.gen, UpdateProjectData()).toErrorResult
    //assert
    result shouldBe Unauthorized.credentialsMissing
  }

  it should "return 401 (Unauthorized) CREDENTIAL_REJECTED if authorization token is not correct" in {
    //act
    val result = Rest.updateProject(ProjectId.gen, UpdateProjectData(), SessionToken.gen).toErrorResult
    //assert
    result shouldBe Unauthorized.credentialsRejected
  }

  it should "return 401 (Unauthorized) CREDENTIAL_REJECTED if user not found" in {
    //arrange
    val userId = UserId.gen
    val session = Mongo.addSession(userId = userId)
    val project = Mongo.addProject(userId = userId)
    //act
    val result = Rest.updateProject(project.id, UpdateProjectData(), session.token).toErrorResult
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
    val result = Rest.updateProject(project.id, UpdateProjectData(), session.token).toErrorResult
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
    val result = Rest.updateProject(projectId, UpdateProjectData(), session.token).toErrorResult
    //assert
    result shouldBe NotFound.projectNotFound
    //cleanup
    Mongo.removeUser(user.id)
    Mongo.removeSession(session.token)
  }
}
