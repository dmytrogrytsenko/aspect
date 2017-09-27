package aspect.rest.user

import aspect.TestBase
import aspect.common._
import aspect.domain.SessionToken
import aspect.rest.RestErrors.Unauthorized
import aspect.rest.models.ProfileResult

class RestGetProfileTest extends TestBase {

  behavior of "GET /profile"

  it should "return user profile successfully" in {
    //arrange
    val user = Mongo.addUser()
    val session = Mongo.addSession(userId = user.id)
    //act
    val result = Rest.getProfile(session.token).to[ProfileResult]
    //assert
    result shouldBe ProfileResult(
      id = user.id,
      name = user.name,
      email = user.email,
      firstName = user.firstName,
      lastName = user.lastName,
      createdAt = user.createdAt)
    //cleanup
    Mongo.removeUser(user.id)
    Mongo.removeSession(session.token)
  }

  it should "return 401 (Unauthorized) CREDENTIALS_MISSING if authorization token is not defined" in {
    //act
    val result = Rest.getProfile().toErrorResult
    //assert
    result shouldBe Unauthorized.credentialsMissing
  }

  it should "return 401 (Unauthorized) CREDENTIALS_REJECTED if authorization token is not correct" in {
    //act
    val result = Rest.getProfile(SessionToken.gen).toErrorResult
    //assert
    result shouldBe Unauthorized.credentialsRejected
  }

  it should "return 401 (Unauthorized) CREDENTIALS_REJECTED if user not found" in {
    //arrange
    val session = Mongo.addSession()
    //act
    val result = Rest.getProfile(session.token).toErrorResult
    //assert
    result shouldBe Unauthorized.credentialsRejected
    //cleanup
    Mongo.removeSession(session.token)
  }

}
