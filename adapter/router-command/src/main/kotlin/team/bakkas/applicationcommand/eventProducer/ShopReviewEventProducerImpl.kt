package team.bakkas.applicationcommand.eventProducer

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import team.bakkas.clientcommand.shop.ShopCommand
import team.bakkas.clientcommand.shopReview.ShopReviewCommand
import team.bakkas.dynamo.shopReview.ShopReview
import team.bakkas.eventinterface.eventProducer.ShopReviewEventProducer
import team.bakkas.eventinterface.kafka.KafkaTopics

// ShopReview에 대한 event producing 로직을 보관하는 클래스
@Component
class ShopReviewEventProducerImpl(
    private val shopReviewKafkaTemplate: KafkaTemplate<String, ShopReview>,
    private val reviewDeletedEventKafkaTemplate: KafkaTemplate<String, ShopReviewCommand.DeletedEvent>
): ShopReviewEventProducer {

    /** review 생성 관련 이벤트 전파를 담당하는 메소드
     * @param createdReview 생성된 shopReview
     */
    override fun propagateCreatedEvent(createdReview: ShopReview): Unit = with(createdReview) {
        // 생성된 review를 redis에서 처리하도록 이벤트 발행
        shopReviewKafkaTemplate.send(KafkaTopics.shopReviewCreateTopic, this)
    }

    /** review 삭제 관련 이벤트 전파를 담당하는 메소드
     * @param deletedEvent 삭제괸 리뷰를 파라미터로 전달
     */
    override fun propagateDeletedEvent(deletedEvent: ShopReviewCommand.DeletedEvent): Unit = with(deletedEvent) {
        // 1. redis에 있는 review cache를 삭제하기 위해 이벤트 발행
        reviewDeletedEventKafkaTemplate.send(KafkaTopics.shopReviewDeleteTopic, this)
    }
}