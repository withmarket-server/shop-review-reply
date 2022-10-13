package team.bakkas.infrastructure.repository.dynamo

import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.CoroutinesUtils
import org.springframework.test.annotation.Rollback
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import team.bakkas.dynamo.shopReview.ShopReview
import java.time.LocalDateTime

/** ShopReviewRepository에 대한 Test Class.
 * @author Brian
 * @since 22/06/02
 * @see <a href="https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/kotlin/services/dynamodb#code-examples">Query method with Global Secondary Index</a>
 */
@SpringBootTest
internal class ShopReviewDynamoRepositoryTest @Autowired constructor(
    private val dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient,
    private val shopReviewDynamoRepository: ShopReviewDynamoRepositoryImpl
) {
    // 테이블 정의
    val asyncTable = dynamoDbEnhancedAsyncClient.table("shop_review", TableSchema.fromBean(ShopReview::class.java))

    /* ==============================[Async Test]============================== */
    @ParameterizedTest
    @CsvSource(value = ["xxxxxx-5120-4ec2-ab92-ca6827428945:진짜 최애 맥주집이에요!!"], delimiter = ':')
    @DisplayName("[Repository] reviewId가 틀려서 못 찾아오는 경우 테스트")
    fun findReviewByIdAndTitleFail1(reviewId: String, reviewTitle: String): Unit = runBlocking {
        // when
        val reviewMono = shopReviewDynamoRepository.findReviewByIdAndTitle(reviewId, reviewTitle)
        val reviewDeferred = CoroutinesUtils.monoToDeferred(reviewMono)
        val review = reviewDeferred.await()

        // then
        assertNull(review)

        println("Test passed!!")
    }

    @ParameterizedTest
    @CsvSource(value = ["b7cbec2d-5120-4ec2-ab92-ca6827428945:진짜 맛없는 맥주집이에요!!"], delimiter = ':')
    @DisplayName("[Repository] reviewName이 틀려서 못 찾아오는 경우 테스트")
    fun findReviewByIdAndTitleFail2(reviewId: String, reviewTitle: String): Unit = runBlocking {
        // when
        val reviewMono = shopReviewDynamoRepository.findReviewByIdAndTitle(reviewId, reviewTitle)
        val reviewDeferred = CoroutinesUtils.monoToDeferred(reviewMono)
        val review = reviewDeferred.await()

        // then
        assertNull(review)

        println("Test passed!!")
    }

    @ParameterizedTest
    @CsvSource(value = ["b7cbec2d-5120-4ec2-ab92-ca6827428945:진짜 최애 맥주집이에요!!"], delimiter = ':')
    @DisplayName("[Repository] 정상적으로 잘 찾아오는 경우 테스트")
    fun findReviewByIdAndTitleSuccess(reviewId: String, reviewTitle: String): Unit = runBlocking {
        // when
        val reviewMono = shopReviewDynamoRepository.findReviewByIdAndTitle(reviewId, reviewTitle)
        val reviewDeferred = CoroutinesUtils.monoToDeferred(reviewMono)
        val review = reviewDeferred.await()

        // then
        assertNotNull(review)
        review?.let {
            assertEquals(it.reviewId, reviewId)
            assertEquals(it.reviewTitle, reviewTitle)
        }

        println("Test passed!!")
    }

    @ParameterizedTest
    @CsvSource(value = ["33daf043-7f36-4a52-b791-018f9d5eb218:역전할머니맥주 영남대점"], delimiter = ':')
    @DisplayName("review들에 대한 flow 테스트")
    fun getAllReviewsSuccess(shopId: String, shopName: String): Unit = runBlocking {
        // given
        val attributeAliasMap = mutableMapOf<String, String>()
        val attributeValueMap = mutableMapOf<String, AttributeValue>()

        attributeAliasMap["#shop_id"] = "shop_id"
        attributeAliasMap["#shop_name"] = "shop_name"

        attributeValueMap[":id_val"] = AttributeValue.fromS(shopId)
        attributeValueMap[":name_val"] = AttributeValue.fromS(shopName)

        // expression 정의
        val expression = Expression.builder()
            .expressionNames(attributeAliasMap)
            .expressionValues(attributeValueMap)
            .expression("#shop_id = :id_val AND #shop_name = :name_val")
            .build()

        // when
        val shopReviewFlow = asyncTable.scan {
            it.filterExpression(expression)
        }.items().asFlow()

        val shopReviewList = mutableListOf<ShopReview>()
        shopReviewFlow.buffer()
            .collect {
                shopReviewList.add(it)
            }

        shopReviewList.forEach {
            println(it)
        }
    }
    @ParameterizedTest
    @CsvSource(value = ["ecdc2-test005:저는 만족 못해요:33daf043-7f36-4a52-b791-018f9d5eb218:역전할머니맥주 영남대점"], delimiter = ':')
    @DisplayName("[Repository] Async 방식으로 review를 하나 생성한다")
    @Rollback(value = false)
    fun createShopAsync(reviewId: String, reviewTitle: String, shopId: String, shopName: String): Unit = runBlocking {
        val mockReview = getMockReview(reviewId, reviewTitle, shopId, shopName)

        val reviewMono = shopReviewDynamoRepository.createReviewAsync(mockReview)
        CoroutinesUtils.monoToDeferred(reviewMono).await()
    }

    // 키를 생성하는 메소드
    private fun generateKey(reviewId: String, reviewTitle: String): Key = Key.builder()
        .partitionValue(reviewId)
        .sortValue(reviewTitle)
        .build()

    // 리뷰를 하나 생성하는 메소드
    private fun getMockReview(reviewId: String, reviewTitle: String, shopId: String, shopName: String) =
        ShopReview(
            reviewId = reviewId,
            reviewTitle = reviewTitle,
            shopId = shopId,
            shopName = shopName,
            reviewContent = "매우 불만족.",
            reviewScore = 1.0,
            reviewPhotoList = listOf(),
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )
}