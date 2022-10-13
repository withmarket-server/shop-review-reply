package team.bakkas.dynamo.shop.vo

import software.amazon.awssdk.enhanced.dynamodb.EnhancedType
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean

/** Shop의 Address 정보를 모아둔 vo
 * @author Doyeop Kim
 * @since 2022/10/13
 */
@DynamoDbBean
data class AddressInfo(
    @get:DynamoDbAttribute("lot_number_address")
    var lotNumberAddress: String = "",
    @get:DynamoDbAttribute("road_name_address")
    var roadNameAddress: String = "",
    @get:DynamoDbAttribute("detail_address")
    var detailAddress: String? = null
): java.io.Serializable {

    companion object {
        private val tableSchema = TableSchema.fromBean(AddressInfo::class.java)

        val enhancedType = EnhancedType.documentOf(AddressInfo::class.java, tableSchema)
    }
}