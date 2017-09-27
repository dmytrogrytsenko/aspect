package aspect.common.mongo

import aspect.common.config.Application
import aspect.common.extensions.FutureExtensions._
import reactivemongo.api.MongoConnection.ParsedURI
import reactivemongo.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object MongoDatabase {
  val uri: ParsedURI = MongoConnection.parseURI(Application.config.getString("aspect.mongo.uri")).get
  val driver: MongoDriver = new MongoDriver
  val connection: MongoConnection = driver.connection(uri)
  val db: DB = connection.database(uri.db.get).await(5.seconds)
}
