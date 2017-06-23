package aspect.common

case class Shard(underlying: Int) {
  require(underlying >= 0 && underlying < Shard.count)
}

object Shard {
  val count = 128
  val all: Set[Shard] = (0 until count).map(Shard.apply).toSet

  def calc[T](value: T): Shard = ???
}
