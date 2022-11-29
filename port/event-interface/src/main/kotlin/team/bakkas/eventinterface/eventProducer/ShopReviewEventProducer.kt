package team.bakkas.eventinterface.eventProducer

import team.bakkas.clientcommand.shopReview.ShopReviewCommand
import team.bakkas.dynamo.shopReview.ShopReview

/**
 * ShopReviewEventProducer
 * ShopReview에 대한 이벤트 발행을 담당하는 interface
 */
interface ShopReviewEventProducer {

    fun propagateCreatedEvent(createdReview: ShopReview)

    fun propagateDeletedEvent(deletedEvent: ShopReviewCommand.DeletedEvent)
}