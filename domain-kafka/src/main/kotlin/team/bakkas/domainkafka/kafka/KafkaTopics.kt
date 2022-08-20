package team.bakkas.domainkafka.kafka

// Kafka에서 사용되는 토픽 이름을 정의한 object class
object KafkaTopics {

    const val shopCreateTopic = "withmarket.shop.create"
    const val shopCountValidateTopic = "withmarket.shop.validate.count" // redis에 캐싱된 shop의 개수가 맞는지 검증하는데 사용하는 토픽
    const val reviewGenerateEventTopic = "withmarket.shop.review.count" // review가 작성되어 shop의 정보를 수정하는데 사용하는 토픽

    const val shopReviewCreateTopic = "withmarket.shopReview.create"
    const val shopReviewDeleteTopic = "withmarket.shopReview.delete"
    const val reviewCountValidateTopic = "withmarket.shopReview.validate.count"
}