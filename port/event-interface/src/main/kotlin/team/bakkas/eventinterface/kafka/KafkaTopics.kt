package team.bakkas.eventinterface.kafka

// Kafka에서 사용되는 토픽 이름을 정의한 object class
object KafkaTopics {

    const val shopCreateTopic = "withmarket.shop.create"
    const val shopDeleteTopic = "withmarket.shop.delete"
    const val shopUpdateTopic = "withmarket.shop.update"

    const val shopReviewCreateTopic = "withmarket.shopReview.create"
    const val shopReviewDeleteTopic = "withmarket.shopReview.delete"
}