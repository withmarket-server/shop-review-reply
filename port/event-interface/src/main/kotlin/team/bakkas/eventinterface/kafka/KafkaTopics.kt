package team.bakkas.eventinterface.kafka

// Kafka에서 사용되는 토픽 이름을 정의한 object class
object KafkaTopics {

    const val shopCreateTopic = "withmarket.shop.create"
    const val reviewGenerateEventTopic = "withmarket.shop.review.count" // review가 작성되어 shop의 정보를 수정하는데 사용하는 토픽

    const val shopReviewCreateTopic = "withmarket.shopReview.create"
    const val shopReviewDeleteTopic = "withmarket.shopReview.delete"
}