package team.bakkas.clientcommand.shopReview

import com.fasterxml.jackson.annotation.JsonProperty
import team.bakkas.clientcommand.shopReview.annotations.ReviewCreatable

// ShopReview에 대한 command에 사용되는 dto들을 정의하는 sealed class
sealed class ShopReviewCommand {

    // shop review를 생성하는데 사용하는 dto class
    @ReviewCreatable
    data class CreateRequest(
        @JsonProperty("review_title") var reviewTitle: String,
        @JsonProperty("shop_id") var shopId: String,
        @JsonProperty("review_content") var reviewContent: String,
        @JsonProperty("review_score") var reviewScore: Double,
        @JsonProperty("review_photo_list") var reviewPhotoList: List<String> = listOf()
    )

    // Review가 삭제될 때 발행하는 이벤트
    data class DeletedEvent(
        var reviewId: String
    ) {
        companion object {
            // DeletedEvent를 생성하는데 사용하는 메소드
            fun of(reviewId: String) = DeletedEvent(reviewId)
        }
    }
}
