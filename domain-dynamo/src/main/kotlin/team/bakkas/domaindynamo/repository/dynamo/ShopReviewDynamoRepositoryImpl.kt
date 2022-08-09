package team.bakkas.domaindynamo.repository.dynamo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import team.bakkas.domaindynamo.entity.ShopReview

/** shop_review 테이블에 대한 repository class
 * @param dynamoDbEnhancedClient
 * @since 22/06/02
 * @author Brian
 * @see <a href="https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/kotlin/services/dynamodb#code-examples">Query method with Global Secondary Index</a>
 */
@Repository
class ShopReviewDynamoRepositoryImpl(
    private val dynamoDbEnhancedClient: DynamoDbEnhancedClient,
    private val dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient
) {
    // shop_review에 대한 테이블 정의
    val table = dynamoDbEnhancedClient.table("shop_review", TableSchema.fromBean(ShopReview::class.java))
    val asyncTable: DynamoDbAsyncTable<ShopReview> =
        dynamoDbEnhancedAsyncClient.table("shop_review", TableSchema.fromBean(ShopReview::class.java))

    /** Shop에 대한 Review를 생성하는 메소드
     * @param shopReview 리뷰 엔티티
     */
    fun createReview(shopReview: ShopReview): ShopReview {
        table.putItem(shopReview)

        return shopReview
    }

    /** reviewId와 reviewTitle을 기반으로 review를 하나 찾아오는 메소드
     * @param reviewId
     * @param reviewTitle
     * @return ShopReview if exists else null
     */
    fun findReviewByIdAndTitle(reviewId: String, reviewTitle: String): ShopReview? {
        val reviewKey = generateKey(reviewId, reviewTitle)

        return table.getItem(reviewKey) ?: null
    }

    /** shopId와 shopName을 기반으로 특정 shop에 달려있는 모든 review를 가져오는 메소드
     * @param shopId shop의 고유 id
     * @param shopName shop의 이름
     */
    fun getReviewListByShopGsi(shopId: String, shopName: String): List<ShopReview> {
        // filter expression에서 조건에 해당하는 변수명을 저장하는 map
        val attributeAliasMap = mutableMapOf<String, String>()
        // filter expression에서 값들을 저장하는 map
        val attributeValueMap = mutableMapOf<String, AttributeValue>()

        // Alias 식을 이용하여 shop_id를 표현
        attributeAliasMap["#shop_id"] = "shop_id"
        // Alias 식을 이용하여 shop_name을 표현
        attributeAliasMap["#shop_name"] = "shop_name"

        // 파라미터로 들어온 id를 저장
        attributeValueMap[":id_val"] = AttributeValue.fromS(shopId)
        // 파라미터로 들어온 name을 저장
        attributeValueMap[":name_val"] = AttributeValue.fromS(shopName)

        // expression 정의
        val expression = Expression.builder()
            .expressionNames(attributeAliasMap)
            .expressionValues(attributeValueMap)
            .expression("#shop_id = :id_val AND #shop_name = :name_val")
            .build()

        // scan 방식으로 테이블을 full-read 하여 조건에 해당하는 리뷰들을 리스트로 반환
        return table.scan {
            it.filterExpression(expression)
        }.items().toList()
    }

    /** reviewId와 reviewTitle을 기반으로 review를 하나 삭제하는
     * @param reviewId
     * @param reviewTitle
     */
    fun deleteReview(reviewId: String, reviewTitle: String) {
        val reviewKey = generateKey(reviewId, reviewTitle)

        table.deleteItem(reviewKey)
    }

    /* ==============================[Async Methods]============================== */

    /** 비동기적으로 review를 하나 가져오는 메소드
     * @param reviewId 리뷰의 id
     * @param reviewTitle 리뷰의 제목
     * @return Mono<ShopReview?>
     */
    fun findReviewByIdAndTitleAsync(reviewId: String, reviewTitle: String): Mono<ShopReview?> {
        val reviewKey = generateKey(reviewId, reviewTitle)
        val reviewFuture = asyncTable.getItem(reviewKey)

        return Mono.fromFuture(reviewFuture)
    }

    /** review들에 대한 Key의 flow를 반환해주는 메소드
     * @param shopId
     * @param shopName
     * @return Flow consisted with Pair of reviewId and reviewTitle (Pair<String, String>)
     */
    fun getAllReviewKeyFlowByShopIdAndName(shopId: String, shopName: String): Flow<Pair<String, String>> {
        val attributeAliasMap = mutableMapOf<String, String>()
        val attributeValueMap = mutableMapOf<String, AttributeValue>()

        attributeAliasMap["#shop_id"] = "shop_id"
        attributeAliasMap["#shop_name"] = "shop_name"

        attributeValueMap[":id_val"] = AttributeValue.fromS(shopId)
        attributeValueMap[":name_val"] = AttributeValue.fromS(shopName)

        val expression = Expression.builder()
            .expressionNames(attributeAliasMap)
            .expressionValues(attributeValueMap)
            .expression("#shop_id = :id_val AND #shop_name = :name_val")
            .build()

        return asyncTable.scan {
            it.filterExpression(expression)
        }.items().asFlow().map {
            Pair(it.reviewId, it.reviewTitle)
        }
    }

    // review를 하나 생성하는 메소드
    fun createReviewAsync(shopReview: ShopReview): Mono<Void> {
        val reviewFuture = asyncTable.putItem(shopReview)

        return Mono.fromFuture(reviewFuture)
    }

    /** Key를 반환하는 private method
     * @param reviewId Partition Key of shop_review
     * @param reviewTitle Sort Key of shop_review
     * @return key of sjop_review table
     */
    private fun generateKey(reviewId: String, reviewTitle: String): Key = Key.builder()
        .partitionValue(reviewId)
        .sortValue(reviewTitle)
        .build()
}