package team.bakkas.common.utils

// Redis를 사용함에 있어서 유틸성 메소드, 혹은 속성들을 정의하는 object class
object RedisUtils {

    const val DAYS_TO_LIVE = 30L

    fun generateShopRedisKey(shopId: String) = "shop:${shopId}"

    fun generateReviewRedisKey(reviewId: String) = "shopReview:${reviewId}"
}