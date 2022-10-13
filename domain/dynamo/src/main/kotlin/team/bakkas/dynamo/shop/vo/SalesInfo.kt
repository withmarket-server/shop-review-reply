package team.bakkas.dynamo.shop.vo

import software.amazon.awssdk.enhanced.dynamodb.EnhancedType
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import team.bakkas.dynamo.shop.vo.sale.Days
import java.time.LocalTime

/** 가게의 open/close 정보를 저장하는 vo
 * @author Doyeop Kim
 * @since 2022/10/13
 */
@DynamoDbBean
data class SalesInfo(
    @get:DynamoDbAttribute("is_open")
    var isOpen: Boolean = false,
    @get:DynamoDbAttribute("open_time")
    var openTime: LocalTime = LocalTime.now(),
    @get:DynamoDbAttribute("close_time")
    var closeTime: LocalTime = LocalTime.now(),
    @get:DynamoDbAttribute("rest_day_list")
    var restDayList: List<Days> = listOf()
) {

    companion object {
        // table schema
        private val tableSchema = TableSchema.fromBean(SalesInfo::class.java)

        val enhancedType = EnhancedType.documentOf(SalesInfo::class.java, tableSchema)
    }
}