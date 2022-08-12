package team.bakkas.applicationcommand.kafka

// Kafka에서 사용되는 토픽 이름을 정의한 object class
object KafkaTopics {

    const val shopCreateTopic = "withmarket.shop.create"
    const val reviewCountEventTopic = "withmarket.shop.review.count"

    const val shopReviewCreateTopic = "withmarket.shopReview.create"
    const val shopReviewDeleteTopic = "withmarket.shopReview.delete"
}