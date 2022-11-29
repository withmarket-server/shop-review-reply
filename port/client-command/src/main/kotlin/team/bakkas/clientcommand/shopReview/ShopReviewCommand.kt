package team.bakkas.clientcommand.shopReview

import com.fasterxml.jackson.annotation.JsonProperty
import team.bakkas.clientcommand.shopReview.annotations.ReviewCreatable

sealed class ShopReviewCommand {

    // ShopReview 생성 request를 정의하는 dto class
    @ReviewCreatable
    data class CreateRequest(
        @field:JsonProperty("review_title") var reviewTitle: String,
        @field:JsonProperty("shop_id") var shopId: String,
        @field:JsonProperty("review_content") var reviewContent: String,
        @field:JsonProperty("review_score") var reviewScore: Double,
        @field:JsonProperty("review_photo_list") var reviewPhotoList: List<String> = listOf()
    )

    // Review가 삭제될 때 발행하는 이벤트를 정의하는 dto class
    data class DeletedEvent(
        var reviewId: String,
        var shopId: String
    ) {
        companion object {
            // DeletedEvent를 생성하는데 사용하는 메소드
            fun of(reviewId: String, shopId: String) = DeletedEvent(reviewId, shopId)
        }
    }
}
