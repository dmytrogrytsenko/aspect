package aspect.rest.user

import aspect.TestBase
import aspect.common._
import aspect.common.Crypt._
import aspect.rest.RestErrors.{BadRequest, Unauthorized}
import aspect.rest.models.{LoginData, LoginResult}

class RestLoginTest extends TestBase {

  behavior of "POST /login"

  it should "create session and return token without case sensitive username" in {
    //arrange
    val user = Mongo.addUser(name = genString + "abcXYZ")
    //act
    val startTime = utc
    val result = Rest.login(LoginData(user.name.toUpperCase, user.password)).to[LoginResult]
    val endTime = utc
    //assert
    val session = Mongo.getSession(result.token).get
    session.token shouldBe result.token
    session.userId shouldBe user.id
    session.createdAt shouldBeInRange (startTime -> endTime)
    session.lastActivityAt shouldBeInRange (startTime -> endTime)
    //cleanup
    Mongo.removeUser(user.id)
    Mongo.removeSession(result.token)
  }

  it should "return 400 (Bad Request) VALIDATION if username is empty" in {
    //act
    val result = Rest.login(LoginData("", genString)).toErrorResult
    //assert
    result shouldBe BadRequest.Validation.requiredMemberEmpty("login")
  }

  it should "return 400 (Bad Request) VALIDATION if password is empty" in {
    //act
    val result = Rest.login(LoginData(genString, "")).toErrorResult
    //assert
    result shouldBe BadRequest.Validation.requiredMemberEmpty("password")
  }

  it should "return 401 (Unauthorized) CREDENTIALS_REJECTED if user not found" in {
    //arrange
    val login = genString
    //act
    val result = Rest.login(LoginData(login, genString)).toErrorResult
    //assert
    result shouldBe Unauthorized.credentialsRejected
  }

  it should "return 401 (Unauthorized) CREDENTIALS_REJECTED if password is incorrect" in {
    //arrange
    val user = Mongo.addUser()
    //act
    val result = Rest.login(LoginData(user.name, genString)).toErrorResult
    //assert
    result shouldBe Unauthorized.credentialsRejected
    //cleanup
    Mongo.removeUser(user.id)
  }
}
