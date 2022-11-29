package team.bakkas.dao.repository.dynamo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.reactive.asFlow
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import team.bakkas.dynamo.shopReview.ShopReview
import team.bakkas.dynamo.shopReview.extensions.softDelete
import team.bakkas.repository.ifs.dynamo.ShopReviewDynamoRepository

/**
 * ShopReviewDynamoRepositoryImpl(private val dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient)
 * ShopReviewDynamoRepository의 구현체
 * @param dynamoDbEnhancedAsyncClient
 * @see <a href="https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/kotlin/services/dynamodb#code-examples">Query method with Global Secondary Index</a>
 */
@Repository
class ShopReviewDynamoRepositoryImpl(
    private val dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient
) : ShopReviewDynamoRepository {
    val asyncTable: DynamoDbAsyncTable<ShopReview> =
        dynamoDbEnhancedAsyncClient.table("shop_review", ShopReview.tableSchema)

    override fun findReviewById(reviewId: String): Mono<ShopReview> {
        val reviewKey = generateKey(reviewId)
        val reviewFuture = asyncTable.getItem(reviewKey)

        return Mono.fromFuture(reviewFuture)
    }

    override fun getAllReviewsByShopId(shopId: String): Flow<ShopReview> {
        return asyncTable.scan { it.filterExpression(generateShopExpression(shopId)) }
            .items()
            .asFlow()
            .filter { it.deletedAt == null } // 삭제된적이 없는 리뷰들만 가져온다
    }

    override fun createReviewAsync(shopReview: ShopReview): Mono<ShopReview> {
        val reviewFuture = asyncTable.putItem(shopReview)

        return Mono.fromFuture(reviewFuture)
            .thenReturn(shopReview)
    }

    override fun deleteReviewAsync(reviewId: String): Mono<ShopReview> {
        val reviewKey = generateKey(reviewId)
        val deleteReviewFuture = asyncTable.deleteItem(generateKey(reviewId))
        return Mono.fromFuture(deleteReviewFuture)
    }

    override fun softDeleteReview(reviewId: String): Mono<ShopReview> {
        return findReviewById(reviewId)
            .map { it.softDelete() }
            .flatMap { createReviewAsync(it) }
    }

    private fun generateKey(reviewId: String): Key = Key.builder()
        .partitionValue(reviewId)
        .build()

    /**
     * 파라미터로 전달되는 shopId가 일치하는 review를 찾아오는 expression 객체를 반환하는 메소드
     * @param shopId shop id
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