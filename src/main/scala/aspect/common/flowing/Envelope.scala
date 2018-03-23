package aspect.common.flowing

import java.util.UUID

import aspect.common.uuid

import scala.reflect.ClassTag

case class EnvelopeId(underlying: UUID) extends AnyVal

object EnvelopeId {
  def gen: EnvelopeId = EnvelopeId(uuid)
}

case class Envelope(id: EnvelopeId, flowIds: Set[FlowId], message: Any, stack: List[Any] = Nil) {
  def is[T: ClassTag]: Boolean = implicitly[ClassTag[T]].runtimeClass.isAssignableFrom(message.getClass)
  def as[T]: T = message.asInstanceOf[T]
}

object Envelope {
  def first(message: Any) = Envelope(EnvelopeId.gen, Set(FlowId.gen), message)
}
