package aspect.common

case class Shard(underlying: Int) {
  require(underlying >= 0 && underlying < Shard.count)
}

object Shard {
  val count: Int = 8
  val all: Set[Shard] = (0 until count).map(Shard.apply).toSet

  implicit class IntShardOps(val value: Int) extends AnyVal {
    def toShard: Shard = Shard(value % count)
  }
}
