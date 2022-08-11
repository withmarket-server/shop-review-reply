package team.bakkas.common.utils

// Redis를 사용함에 있어서 유틸성 메소드, 혹은 속성들을 정의하는 object class
object RedisUtils {

    const val DAYS_TO_LIVE = 1L

    fun generateShopRedisKey(shopId: String, shopName: String) = "shop:${shopId}-${shopName}"

    fun generateReviewRedisKey(reviewId: String, reviewTitle: String) = "shopReview-$reviewId-$reviewTitle"
}