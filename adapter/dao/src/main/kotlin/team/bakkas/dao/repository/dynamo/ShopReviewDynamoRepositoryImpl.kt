package team.bakkas.dao.repository.dynamo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import team.bakkas.dynamo.shopReview.ShopReview
import team.bakkas.dynamo.shopReview.usecases.softDelete
import team.bakkas.repository.ifs.dynamo.ShopReviewDynamoRepository

/** shop_review 테이블에 대한 repository class
 * @param dynamoDbEnhancedClient
 * @since 22/06/02
 * @author Brian
 * @see <a href="https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/kotlin/services/dynamodb#code-examples">Query method with Global Secondary Index</a>
 */
@Repository
class ShopReviewDynamoRepositoryImpl(
    private val dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient
) : ShopReviewDynamoRepository {
    val asyncTable: DynamoDbAsyncTable<ShopReview> =
        dynamoDbEnhancedAsyncClient.table("shop_review", ShopReview.tableSchema)

    /* ==============================[Async Methods]============================== */

    /** 비동기적으로 review를 하나 가져오는 메소드
     * @param reviewId 리뷰의 id
     * @param reviewTitle 리뷰의 제목
     * @return Mono<ShopReview?>
     */
    override fun findReviewByIdAndTitle(reviewId: String): Mono<ShopReview> {
        val reviewKey = generateKey(reviewId)
        val reviewFuture = asyncTable.getItem(reviewKey)

        return Mono.fromFuture(reviewFuture)
    }

    /** shop에 대한 review flow를 반환해주는 메소드
     * @param shopId
     * @param shopName
     * @return Flow consisted with review of given shop
     */
    override fun getAllShopsByShopId(shopId: String): Flow<ShopReview> {
        return asyncTable.scan { it.filterExpression(generateShopExpression(shopId)) }
            .items()
            .asFlow()
    }

    // review를 하나 생성하는 메소드
    override fun createReviewAsync(shopReview: ShopReview): Mono<ShopReview> {
        val reviewFuture = asyncTable.putItem(shopReview)

        return Mono.fromFuture(reviewFuture)
            .thenReturn(shopReview)
    }

    // review를 삭제하는 메소드
    override fun deleteReviewAsync(reviewId: String): Mono<ShopReview> {
        val reviewKey = generateKey(reviewId)
        val deleteReviewFuture = asyncTable.deleteItem(generateKey(reviewId))
        return Mono.fromFuture(deleteReviewFuture)
    }

    // review를 soft delete 하는 메소드
    override fun softDeleteReview(reviewId: String): Mono<ShopReview> {
        return findReviewByIdAndTitle(reviewId)
            .map { it.softDelete() }
            .flatMap { createReviewAsync(it) }
    }

    /** Key를 반환하는 private method
     * @param reviewId Partition Key of shop_review
     * @return key of sjop_review table
     */
    private fun generateKey(reviewId: String): Key = Key.builder()
        .partitionValue(reviewId)
        .build()

    /** shopId, shopName에 대한 expression을 반환해주는 메소드
     * @param shopId
     * @param shopName
     */
    private fun generateShopExpression(shopId: String): Expression {
        val attributeAliasMap = mutableMapOf<String, String>()
        val attributeValueMap = mutableMapOf<String, AttributeValue>()

        attributeAliasMap["#shop_id"] = "shop_id"

        attributeValueMap[":id_val"] = AttributeValue.fromS(shopId)

        return Expression.builder()
            .expressionNames(attributeAliasMap)
            .expressionValues(attributeValueMap)
            .expression("#shop_id = :id_val")
            .build()
    }
}