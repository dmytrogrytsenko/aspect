package aspect.mongo

import aspect.TestBase
import reactivemongo.api.indexes.{Index, IndexType}

import scala.concurrent.ExecutionContext.Implicits.global

class UserCollectionTest extends TestBase {

  behavior of "ensureIndexes"

  it should "ensure indexes" in {
    //arrange
    Mongo.users.dropIndexes.await
    //act
    Mongo.users.ensureIndexes.await
    //assert
    val indexes = Mongo.users.items.indexesManager.list().await
    indexes should contain (Index(Seq("nameLC" -> IndexType.Ascending), name = Some("nameLC_1"), unique = true, version = Some(2)))
  }

  behavior of "findUserByName"

  it should "return user by name" in {
    //arrange
    val user = Mongo.addUser()
    //act
    val result = Mongo.users.findUserByName(user.name).await
    //assert
    result.value shouldBe user
    //cleanup
    Mongo.removeUser(user.id)
  }

  it should "return user by name and ignore case" in {
    //arrange
    val user = Mongo.addUser(name = genString + "ABC")
    //act
    val result = Mongo.users.findUserByName(user.name.toUpperCase).await
    //assert
    result.value shouldBe user
    //cleanup
    Mongo.removeUser(user.id)
  }

  it should "return None if user not found by name" in {
    //act
    val result = Mongo.users.findUserByName(genString).await
    //assert
    result shouldBe None
  }

}
