package team.bakkas.clientquery.shopReview

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

sealed class ShopReviewQuery {

    /** shopReview에 대한 기본적인 dto
     * @author Brian
     * @since 22/06/03
     */
    data class SimpleResponse(
        @JsonProperty("review_id") var reviewId: String,
        @JsonProperty("review_title") var reviewTitle: String,
        @JsonProperty("shop_id") var shopId: String,
        @JsonProperty("shop_name") var shopName: String,
        @JsonProperty("review_content") var reviewContent: String,
        @JsonProperty("review_score") var reviewScore: Double,
        @JsonProperty("review_photo_list") var reviewPhotoList: List<String>,
        @JsonProperty("created_at") var createdAt: LocalDateTime,
        @JsonProperty("updated_at") var updatedAt: LocalDateTime?
    )

    // ShopReview 목록을 조회 시 발행되는 event에 실을 count DTO
    data class CountEvent(
        var count: Int,
        var shopId: String,
        var shopName: String
    )
}
