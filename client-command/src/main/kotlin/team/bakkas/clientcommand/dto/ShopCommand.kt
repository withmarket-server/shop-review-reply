package team.bakkas.clientcommand.dto

import com.fasterxml.jackson.annotation.JsonProperty
import team.bakkas.common.category.Category
import team.bakkas.common.category.DetailCategory
import java.time.LocalTime

sealed class ShopCommand {
    /** Shop을 생성하는데 사용하는 dto class
     * @since 22.07.24
     */
    data class ShopCreateDto(
        @JsonProperty("shop_name") var shopName: String,
        @JsonProperty("open_time") var openTime: LocalTime,
        @JsonProperty("close_time") var closeTime: LocalTime,
        @JsonProperty("lot_number_address") var lotNumberAddress: String,
        @JsonProperty("road_name_address") var roadNameAddress: String,
        @JsonProperty("latitude") var latitude: Double,
        @JsonProperty("longitude") var longitude: Double,
        @JsonProperty("shop_description") var shopDescription: String,
        @JsonProperty("is_branch") var isBranch: Boolean,
        @JsonProperty("branch_name") var branchName: String? = null,
        @JsonProperty("shop_category") var shopCategory: Category,
        @JsonProperty("shop_detail_category") var shopDetailCategory: DetailCategory,
        @JsonProperty("main_image_url") var mainImageUrl: String?,
        @JsonProperty("representative_image_url") var representativeImageUrlList: List<String>
    )

    /** shop에 대한 review가 작성되거나, 혹은 삭제되었을 때의 이벤트를 처리하는 dto
     * @param shopId shop의 id
     * @param shopName shop의 name
     * @param isGenerated shop의 리뷰가 작성되었는지, 아니면 삭제되었는지 여부를 저장하는 변수
     */
    data class ReviewCountEventDto(
        var shopId: String,
        var shopName: String,
        var isGenerated: Boolean,
        var reviewScore: Double
    )
}
