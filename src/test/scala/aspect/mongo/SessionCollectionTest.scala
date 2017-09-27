package aspect.mongo

import aspect.common._
import aspect.domain.SessionToken
import aspect.TestBase

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class SessionCollectionTest extends TestBase {

  behavior of "activity"

  it should "update lastActivityAt with DateTime.now" in {
    //arrange
    val session = Mongo.addSession(lastActivityAt = utc - 5.minutes)
    //act
    val startTime = utc
    Mongo.sessions.activity(session.token).await
    val endTime = utc
    //assert
    Mongo.getSession(session.token).get.lastActivityAt shouldBeInRange (startTime -> endTime)
    //cleanup
    Mongo.removeSession(session.token)
  }

  it should "do nothing if no token found" in {
    //act
    Mongo.sessions.activity(SessionToken.gen).await
  }
}
