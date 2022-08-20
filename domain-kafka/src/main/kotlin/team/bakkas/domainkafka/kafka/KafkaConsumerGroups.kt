package team.bakkas.domainkafka.kafka

object KafkaConsumerGroups {

    const val createShopGroup = "withmarket-shop-create-group"

    const val updateShopReviewCountGroup = "withmarket-shop-review-count-group"

    // shop의 개수가 정합성을 이뤄주고 있는지 체크하는 컨슈머 그룹
    const val checkShopCountGroup = "withmarket-shop-count-group"

    const val createShopReviewGroup = "withmarket-shopReview-create-group"

    const val deleteShopReviewGroup = "withmarket-shopReview-delete-group"

    // shop에 대한 shopReview 개수가 정합을 이루는지 체크하는 컨슈머 그룹
    const val checkShopReviewCountGroup = "withmarket-shopReview-validate-count-group"
}