package team.bakkas.clientcommand.shop

import com.fasterxml.jackson.annotation.JsonProperty
import team.bakkas.clientcommand.shop.annotations.ShopCreatable
import team.bakkas.dynamo.shop.vo.DeliveryTipPerDistance
import team.bakkas.dynamo.shop.vo.category.Category
import team.bakkas.dynamo.shop.vo.category.DetailCategory
import team.bakkas.dynamo.shop.vo.sale.Days
import java.time.LocalTime

sealed class ShopCommand {

    // Shop을 생성하는데 사용되는 request를 정의하는 class
    @ShopCreatable
    data class CreateRequest(
        @field:JsonProperty("shop_name") var shopName: String,
        @field:JsonProperty("member_id") var memberId: String,
        @field:JsonProperty("open_time") var openTime: LocalTime,
        @field:JsonProperty("close_time") var closeTime: LocalTime,
        @field:JsonProperty("rest_day_list") var restDayList: List<Days>,
        @field:JsonProperty("lot_number_address") var lotNumberAddress: String,
        @field:JsonProperty("road_name_address") var roadNameAddress: String,
        @field:JsonProperty("detail_address") var detailAddress: String?,
        @field:JsonProperty("latitude") var latitude: Double,
        @field:JsonProperty("longitude") var longitude: Double,
        @field:JsonProperty("shop_description") var shopDescription: String,
        @field:JsonProperty("is_branch") var isBranch: Boolean,
        @field:JsonProperty("branch_name") var branchName: String? = null,
        @field:JsonProperty("category") var shopCategory: Category,
        @field:JsonProperty("detail_category") var shopDetailCategory: DetailCategory,
        @field:JsonProperty("main_image_url") var mainImageUrl: String?,
        @field:JsonProperty("representative_image_url_list") var representativeImageUrlList: List<String>,
        @field:JsonProperty("delivery_tip_per_distance_list") var deliveryTipPerDistanceList: List<DeliveryTipPerDistance>
    )

    // Shop을 수정하는데 사용하는 dto class
    // must required: shop_id
    // partially required: others (shop_name, main_image, representative_image_url_list, open_time_range, rest_day_list)
    data class UpdateRequest(
        @field:JsonProperty("shop_id") var shopId: String,
        @field:JsonProperty("shop_name") var shopName: String?,
        @field:JsonProperty("main_image") var mainImage: String?,
        @field:JsonProperty("representative_image_url_list") var representativeImageUrlList: List<String>?,
        @field:JsonProperty("open_time_range") var openTimeRange: OpenTimeRange?,
        @field:JsonProperty("rest_day_list") var restDayList: List<Days>?
    )

    // 가게의 여닫는 시간 정보를 저장하는 data class
    // openTime: 여는 시간 format: 00:00:00
    // closeTime: 닫는 시간 format: 00:00:00
    data class OpenTimeRange(
        @field:JsonProperty("open_time") var openTime: LocalTime,
        @field:JsonProperty("close_time") var closeTime: LocalTime
    )

    // 가게 삭제 이벤트를 정의하는 클래스
    data class DeletedEvent(
        var shopId: String
    ) {
        companion object {
            // constructor
            fun of(shopId: String) = DeletedEvent(shopId)
        }
    }
}