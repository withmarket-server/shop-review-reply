package team.bakkas.dynamo.shop.vo

import software.amazon.awssdk.enhanced.dynamodb.EnhancedType
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean

/** 거리별 배달료 정보를 저장하는 vo
 * @author Doyeop Kim
 * @since 2022/10/13
 */
@DynamoDbBean
data class DeliveryTipPerDistance(
    @get:DynamoDbAttribute("distance")
    var distance: Double = 0.0,
    @get:DynamoDbAttribute("price")
    var price: Int = 0
) {
    companion object {
        val tableSchema = TableSchema.fromBean(DeliveryTipPerDistance::class.java)

        val enhancedType = EnhancedType.documentOf(DeliveryTipPerDistance::class.java, tableSchema)
    }
}