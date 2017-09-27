package aspect.common.mongo

import reactivemongo.api.DB
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.indexes.Index
import reactivemongo.bson._

import scala.concurrent.{ExecutionContext, Future}

trait MongoCollection[Id, Entity] extends BsonDsl {

  def db: DB

  def name: String

  lazy val items: BSONCollection = db[BSONCollection](name)

  def indexes: List[Index] = Nil

  def ensureIndexes(implicit ec: ExecutionContext): Future[Unit] =
    Future.sequence(indexes.map(items.indexesManager.ensure)).map(_ => {})

  def dropIndexes(implicit ec: ExecutionContext): Future[Unit] =
    items.indexesManager.dropAll().map(_ => {})

//  implicit object DurationReader extends BSONReader[BSONString, Duration] {
//    def read(bson: BSONString): Duration = Duration(bson.value)
//  }
//
//  implicit object DurationWriter extends BSONWriter[Duration, BSONString] {
//    def write(value: Duration) = BSONString(value.toString)
//  }
//
//  implicit object ShardReader extends BSONReader[BSONInteger, Shard] {
//    def read(bson: BSONInteger): Shard = Shard(bson.value)
//  }
//
//  implicit object ShardWriter extends BSONWriter[Shard, BSONInteger] {
//    def write(value: Shard): BSONInteger = BSONInteger(value.underlying)
//  }
//
//  def all(implicit db: DB,
//          reader: BSONDocumentReader[TEntity],
//          executionContext: ExecutionContext): Future[List[TEntity]] =
//    items.find($empty).cursor[TEntity]().collect[List]()
//
  def get(id: Id)
         (implicit identityWriter: BSONWriter[Id, BSONString],
          entityReader: BSONDocumentReader[Entity],
          executionContext: ExecutionContext): Future[Option[Entity]] =
    items.find($id(id)).one[Entity]

//
////  def get(shards: Set[Shard])
////         (implicit db: DB,
////          reader: BSONDocumentReader[TEntity],
////          executionContext: ExecutionContext): Future[List[TEntity]] =
////    items.find($doc("shard" $in (shards.toSeq: _*))).cursor[TEntity]().collect[List]()
////
  def add(entity: Entity)
         (implicit entityWriter: BSONDocumentWriter[Entity],
          executionContext: ExecutionContext): Future[Unit] =
    items.insert(entity).map(_ => { })
//
//  def update(id: TId, entity: TEntity)
//         (implicit db: DB,
//          identityWriter: BSONWriter[TId, BSONString],
//          writer: BSONDocumentWriter[TEntity],
//          executionContext: ExecutionContext): Future[Unit] =
//    items.update($id(id), entity).map(_ => { })
//
  def remove(id: Id)
            (implicit identityWriter: BSONWriter[Id, BSONString],
             executionContext: ExecutionContext): Future[Unit] =
    items.remove($id(id)).map(_ => {})
}
