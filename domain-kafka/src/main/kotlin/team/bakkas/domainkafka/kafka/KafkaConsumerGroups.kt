package team.bakkas.domainkafka.kafka

object KafkaConsumerGroups {

    const val createShopGroup = "withmarket-shop-create-group"
    const val updateShopReviewCountGroup = "withmarket-shop-review-count-group"
    const val checkShopCountGroup = "withmarket-shop-count-group" // shop의 개수가 정합성을 이뤄주고 있는지 체크하는 컨슈머 그룹

    const val createShopReviewGroup = "withmarket-shopReview-create-group"
    const val deleteShopReviewGroup = "withmarket-shopReview-delete-group"
}