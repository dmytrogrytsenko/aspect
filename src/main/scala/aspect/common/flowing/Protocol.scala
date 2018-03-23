package aspect.common.flowing

import java.util.UUID

import aspect.common.uuid

import scala.collection.immutable.Queue

object Protocol {
  case class RequestId(underlying: UUID) extends AnyVal

  object RequestId {
    def gen: RequestId = RequestId(uuid)
  }

  case class PublishId(underlying: UUID) extends AnyVal

  object PublishId {
    def gen: PublishId = PublishId(uuid)
  }

  case class Request(id: RequestId)
  case class RequestMany(id: RequestId, amount: Int)

  case class RequestPending(id: RequestId)
  case class Handle(id: RequestId, envelope: Envelope)
  case class HandleMany(id: RequestId, envelopes: Queue[Envelope])

  case class HandlePending(id: RequestId)
  case class HandleCompleted(id: RequestId)
  case class HandleFailed(id: RequestId, exception: Throwable)

  case class Publish(id: PublishId, envelope: Envelope)
  case class PublishMany(id: PublishId, envelopes: Queue[Envelope])

  case class PublishPending(id: PublishId)
  case class PublishAccepted(id: PublishId)
  case class PublishFailed(id: PublishId, exception: Throwable)
}
