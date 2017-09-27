package aspect.rest.user

import aspect.TestBase
import aspect.common._
import aspect.domain.SessionToken
import aspect.rest.RestErrors.Unauthorized

class RestLogoutTest extends TestBase {

  behavior of "POST /logout"

  it should "delete session" in {
    //arrange
    val session = Mongo.addSession()
    //act
    Rest.logout(session.token).shouldBeOK()
    //assert
    Mongo.getSession(session.token) shouldBe None
  }

  it should "do nothing if session not found" in {
    //act
    Rest.logout(SessionToken.gen).shouldBeOK()
  }

  it should "return 401 (Unauthorized) CREDENTIALS_MISSING if authorization token is not defined" in {
    //act
    val result = Rest.logout().toErrorResult
    //assert
    result shouldBe Unauthorized.credentialsMissing
  }
}
