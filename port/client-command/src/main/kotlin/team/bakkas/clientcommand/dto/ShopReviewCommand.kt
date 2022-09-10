package team.bakkas.clientcommand.dto

import com.fasterxml.jackson.annotation.JsonProperty

// ShopReview에 대한 command에 사용되는 dto들을 정의하는 sealed class
sealed class ShopReviewCommand {

    // shop review를 생성하는데 사용하는 dto class
    data class CreateRequest(
        @JsonProperty("review_title") var reviewTitle: String,
        @JsonProperty("shop_id") var shopId: String,
        @JsonProperty("shop_name") var shopName: String,
        @JsonProperty("review_content") var reviewContent: String,
        @JsonProperty("review_score") var reviewScore: Double,
        @JsonProperty("review_photo_list") var reviewPhotoList: List<String> = listOf()
    )
}
