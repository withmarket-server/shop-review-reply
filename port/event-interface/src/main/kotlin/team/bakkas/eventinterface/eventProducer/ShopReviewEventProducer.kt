package team.bakkas.eventinterface.eventProducer

import team.bakkas.clientcommand.shopReview.ShopReviewCommand
import team.bakkas.dynamo.shopReview.ShopReview

interface ShopReviewEventProducer {

    // shopReview가 생성되었을 때의 이벤트 전파
    fun propagateCreatedEvent(createdReview: ShopReview): Unit

    // shopReview가 삭제되었을 때의 이벤트 전파
    fun propagateDeletedEvent(deletedEvent: ShopReviewCommand.DeletedEvent)
}