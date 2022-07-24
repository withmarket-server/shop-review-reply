package team.bakkas.clientcommand.dto.shop

import com.fasterxml.jackson.annotation.JsonProperty
import team.bakkas.common.category.Category
import team.bakkas.common.category.DetailCategory
import java.time.LocalDateTime
import java.time.LocalTime

/** Shop을 생성하는데 사용하는 dto class
 * @since 22.07.24
 */
data class ShopCreateDto(
    @JsonProperty("shop_name")
    var shopName: String,
    @JsonProperty("open_time")
    var openTime: LocalTime,
    @JsonProperty("close_time")
    var closeTime: LocalTime,
    @JsonProperty("lot_number_address")
    var lotNumberAddress: String,
    @JsonProperty("road_name_address")
    var roadNameAddress: String,
    @JsonProperty("latitude")
    var latitude: Double,
    @JsonProperty("longitude")
    var longitude: Double,
    @JsonProperty("shop_description")
    var shopDescription: String,
    @JsonProperty("is_branch")
    var isBranch: Boolean,
    @JsonProperty("branch_name")
    var branchName: String? = null,
    @JsonProperty("shop_category")
    var shopCategory: Category,
    @JsonProperty("shop_detail_category")
    var shopDetailCategory: DetailCategory
)
