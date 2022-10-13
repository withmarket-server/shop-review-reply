package team.bakkas.dynamo.shop.vo

import software.amazon.awssdk.enhanced.dynamodb.EnhancedType
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean

/** Shop의 본점, 분점 정보를 저장하는 vo
 * @author Doyeop Kim
 * @since 2022/10/13
 */
@DynamoDbBean
data class BranchInfo(
    @get:DynamoDbAttribute("is_branch")
    var isBranch: Boolean = false,
    @get:DynamoDbAttribute("branch_name")
    var branchName: String? = null
) {

    companion object {
        val tableSchema = TableSchema.fromBean(BranchInfo::class.java)

        val enhancedType = EnhancedType.documentOf(BranchInfo::class.java, tableSchema)
    }
}