package team.bakkas.clientquery.shop

import com.fasterxml.jackson.annotation.JsonProperty
import team.bakkas.dynamo.shop.vo.category.Category
import team.bakkas.dynamo.shop.vo.category.DetailCategory
import team.bakkas.dynamo.shop.vo.sale.Status

sealed class ShopQuery {

    // Shop에 대한 간략한 정보를 정의하는 dto class
    data class SimpleResponse(
        @field:JsonProperty("shop_id") var shopId: String,
        @field:JsonProperty("shop_name") var shopName: String,
        @field:JsonProperty("status") var status: Status,
        @field:JsonProperty("lot_number_address") var lotNumberAddress: String,
        @field:JsonProperty("road_name_address") var roadNameAddress: String,
        @field:JsonProperty("latitude") var latitude: Double,
        @field:JsonProperty("longitude") var longitude: Double,
        @field:JsonProperty("average_score") var averageScore: Double,
        @field:JsonProperty("review_number") var reviewNumber: Int,
        @field:JsonProperty("main_image") var mainImage: String?,
        @field:JsonProperty("description") var shopDescription: String?,
        @field:JsonProperty("category") var shopCategory: Category,
        @field:JsonProperty("detail_category") var shopDetailCategory: DetailCategory,
        @field:JsonProperty("is_branch") var isBranch: Boolean,
        @field:JsonProperty("branch_name") var branchName: String?
    )
}
