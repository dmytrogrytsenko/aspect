package aspect

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import akka.testkit.TestKit
import aspect.common.extensions.FutureExtensions
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, OptionValues}

import scala.concurrent.duration._

abstract class TestBase extends TestKit(ActorSystem("test"))
  with FlatSpecLike
  with BeforeAndAfterAll
  with OptionValues
  with RestSupport
  with MongoSupport
  with DateTimeRangeSupport
  with EntityGenerators
  with FutureExtensions {

  implicit val materializer: Materializer = ActorMaterializer()
  implicit val timeout: Duration = 3.seconds

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }
}
