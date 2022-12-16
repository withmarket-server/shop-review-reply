package team.bakkas.clientquery.shopReview

import com.fasterxml.jackson.annotation.JsonProperty
import team.bakkas.clientquery.reply.ReplyQuery
import java.time.LocalDateTime

sealed class ShopReviewQuery {

    // ShopReview에 대한 간략한 정보를 반환하는데 사용하는 dto class
    data class SimpleResponse(
        @field:JsonProperty("review_id") var reviewId: String,
        @field:JsonProperty("review_title") var reviewTitle: String,
        @field:JsonProperty("shop_id") var shopId: String,
        @field:JsonProperty("review_content") var reviewContent: String,
        @field:JsonProperty("review_score") var reviewScore: Double,
        @field:JsonProperty("review_photo_list") var reviewPhotoList: List<String>,
        @field:JsonProperty("created_at") var createdAt: LocalDateTime
    )

    // Reply를 포함한 정보를 반환하는데 사용하는 dto class
    data class WithReplyResponse(
        @field:JsonProperty("review_id") var reviewId: String,
        @field:JsonProperty("review_title") var reviewTitle: String,
        @field:JsonProperty("shop_id") var shopId: String,
        @field:JsonProperty("review_content") var reviewContent: String,
        @field:JsonProperty("review_score") var reviewScore: Double,
        @field:JsonProperty("review_photo_list") var reviewPhotoList: List<String>,
        @field:JsonProperty("created_at") var createdAt: LocalDateTime,
        @field:JsonProperty("reply") var reply: ReplyQuery.SimpleResponse?
    )
}
