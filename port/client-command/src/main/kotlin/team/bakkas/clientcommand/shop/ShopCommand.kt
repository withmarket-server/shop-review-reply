package team.bakkas.clientcommand.shop

import com.fasterxml.jackson.annotation.JsonProperty
import team.bakkas.clientcommand.shop.annotations.ShopCreatable
import team.bakkas.dynamo.shop.vo.DeliveryTipPerDistance
import team.bakkas.dynamo.shop.vo.category.Category
import team.bakkas.dynamo.shop.vo.category.DetailCategory
import team.bakkas.dynamo.shop.vo.sale.Days
import java.time.LocalTime

sealed class ShopCommand {
    /** Shop을 생성하는데 사용하는 dto class
     * @since 22.07.24
     */
    @ShopCreatable
    data class CreateRequest(
        @JsonProperty("shop_name") var shopName: String,
        @JsonProperty("business_number") var businessNumber: String,
        @JsonProperty("open_time") var openTime: LocalTime,
        @JsonProperty("close_time") var closeTime: LocalTime,
        @JsonProperty("rest_day_list") var restDayList: List<Days>,
        @JsonProperty("lot_number_address") var lotNumberAddress: String,
        @JsonProperty("road_name_address") var roadNameAddress: String,
        @JsonProperty("detail_address") var detailAddress: String?,
        @JsonProperty("latitude") var latitude: Double,
        @JsonProperty("longitude") var longitude: Double,
        @JsonProperty("shop_description") var shopDescription: String,
        @JsonProperty("is_branch") var isBranch: Boolean,
        @JsonProperty("branch_name") var branchName: String? = null,
        @JsonProperty("category") var shopCategory: Category,
        @JsonProperty("detail_category") var shopDetailCategory: DetailCategory,
        @JsonProperty("main_image_url") var mainImageUrl: String?,
        @JsonProperty("representative_image_url_list") var representativeImageUrlList: List<String>,
        @JsonProperty("delivery_tip_per_distance_list") var deliveryTipPerDistanceList: List<DeliveryTipPerDistance>
    )

    // Shop을 수정하는데 사용하는 dto class
    data class UpdateRequest(
        @field:JsonProperty("shop_id") var shopId: String,
        @field:JsonProperty("shop_name") var shopName: String?,
        @field:JsonProperty("main_image") var mainImage: String?,
        @field:JsonProperty("representative_image_url_list") var representativeImageUrlList: List<String>?,
        @field:JsonProperty("open_time_range") var openTimeRange: OpenTimeRange?,
        @field:JsonProperty("rest_day_list") var restDayList: List<Days>?
    )

    // 가게의 여닫는 시간 정보를 저장하는 data class
    data class OpenTimeRange(
        @field:JsonProperty("open_time") var openTime: LocalTime,
        @field:JsonProperty("close_time") var closeTime: LocalTime
    )

    data class DeletedEvent(
        var shopId: String
    ) {
        companion object {
            // constructor
            fun of(shopId: String) = DeletedEvent(shopId)
        }
    }
}