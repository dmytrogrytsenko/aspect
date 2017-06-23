package aspect.common

case class Role(underlying: String) {
  require(underlying.forall(_.isLetterOrDigit))
}

