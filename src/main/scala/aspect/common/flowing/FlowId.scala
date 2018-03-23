package aspect.common.flowing

import java.util.UUID

import aspect.common.uuid

case class FlowId(underlying: UUID) extends AnyVal

object FlowId {
  def gen: FlowId = FlowId(uuid)
}
