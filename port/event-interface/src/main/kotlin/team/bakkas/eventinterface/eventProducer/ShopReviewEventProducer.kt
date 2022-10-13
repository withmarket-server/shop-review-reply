package team.bakkas.eventinterface.eventProducer

import team.bakkas.dynamo.entity.ShopReview

interface ShopReviewEventProducer {

    // shopReview가 생성되었을 때의 이벤트 전파
    fun propagateCreatedEvent(createdReview: ShopReview): Unit

    // shopReview가 삭제되었을 때의 이벤트 전파
    fun propagateDeletedEvent(deletedReview: ShopReview): Unit
}