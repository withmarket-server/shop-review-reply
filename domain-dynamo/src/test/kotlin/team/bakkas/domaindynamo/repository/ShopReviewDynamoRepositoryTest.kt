package team.bakkas.domaindynamo.repository

import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.CoroutinesUtils
import org.springframework.test.annotation.Rollback
import org.springframework.util.StopWatch
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import team.bakkas.domaindynamo.entity.ShopReview
import java.time.LocalDateTime
import java.util.*

/** ShopReviewRepository에 대한 Test Class.
 * @author Brian
 * @since 22/06/02
 * @see <a href="https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/kotlin/services/dynamodb#code-examples">Query method with Global Secondary Index</a>
 */
@SpringBootTest
internal class ShopReviewDynamoRepositoryTest @Autowired constructor(
    private val dynamoDbEnhancedClient: DynamoDbEnhancedClient,
    private val dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient,
    private val shopReviewDynamoRepository: ShopReviewDynamoRepository
) {
    // 테이블 정의
    val table = dynamoDbEnhancedClient.table("shop_review", TableSchema.fromBean(ShopReview::class.java))
    val asyncTable = dynamoDbEnhancedAsyncClient.table("shop_review", TableSchema.fromBean(ShopReview::class.java))

    // ============================== [create] ==============================

    @ParameterizedTest
    @CsvSource(value = ["ecdc2-test001:대만족이에요!:33daf043-7f36-4a52-b791-018f9d5eb218:역전할머니맥주 영남대점"], delimiter = ':')
    @DisplayName("table을 이용해서 데이터를 넣어보자!")
    @Rollback(value = false)
    fun createShopWithClient(reviewId: String, reviewTitle: String, shopId: String, shopName: String) {
        val mockReview = getMockReview(reviewId, reviewTitle, shopId, shopName)

        table.putItem(mockReview)
    }

    @ParameterizedTest
    @CsvSource(value = ["ecdc2-test002:진짜 노맛 맥주집이에요!!:33daf043-7f36-4a52-b791-018f9d5eb218:역전할머니맥주 영남대점"], delimiter = ':')
    @DisplayName("repository을 이용해서 데이터를 넣어보자!")
    @Rollback(value = false)
    fun createShopWithRepository(reviewId: String, reviewTitle: String, shopId: String, shopName: String) {
        // given
        val mockReview = getMockReview(UUID.randomUUID().toString(), reviewTitle, shopId, shopName)

        // when
        val createdReview = shopReviewDynamoRepository.createReview(mockReview)

        // then
        with(createdReview) {
            assertNotNull(this)
            assertEquals(reviewTitle, this.reviewTitle)
            assertEquals(shopId, this.shopId)
            assertEquals(shopName, this.shopName)
        }

        println("Test passed!!")
    }

    // ============================== [find] ==============================

    @ParameterizedTest
    @CsvSource(value = ["6274cfba-92a7-4e44-9a8f-123113e928bc:대만족이에요!"], delimiter = ':')
    @DisplayName("client를 이용해서 find 테스트. (Success)")
    fun clientSuccessFindReviewWithIdAndName(reviewId: String, reviewTitle: String) {
        // given
        val reviewKey = generateKey(reviewId, reviewTitle)

        // when
        val foundReview = table.getItem(reviewKey)

        // then
        with(foundReview) {
            assertNotNull(this)
            assertEquals(reviewId, this.reviewId)
            assertEquals(reviewTitle, this.reviewTitle)
        }

        println("Test passed!!")
        println(foundReview)
    }

    @ParameterizedTest
    @CsvSource(value = ["가짜아이디:대만족이에요!"], delimiter = ':')
    @DisplayName("client를 이용해서 find 테스트. reviewId를 잘못 주는 케이스(Fail)")
    fun clientFailureFindReviewWithIdAndName1(reviewId: String, reviewTitle: String) {
        // given
        val reviewKey = generateKey(reviewId, reviewTitle)

        // when
        val foundReview = table.getItem(reviewKey)

        // then
        with(foundReview) {
            assertNull(this)
        }

        println("Test passed!!")
    }

    @ParameterizedTest
    @CsvSource(value = ["6274cfba-92a7-4e44-9a8f-123113e928bc:불만족이에요!"], delimiter = ':')
    @DisplayName("client를 이용해서 find 테스트. reviewTitle를 잘못 주는 케이스(Fail)")
    fun clientFailureFindReviewWithIdAndName2(reviewId: String, reviewTitle: String) {
        // given
        val reviewKey = generateKey(reviewId, reviewTitle)

        // when
        val foundReview = table.getItem(reviewKey)

        // then
        with(foundReview) {
            assertNull(this)
        }

        println("Test passed!!")
    }

    @ParameterizedTest
    @CsvSource(value = ["b7cbec2d-5120-4ec2-ab92-ca6827428945:진짜 최애 맥주집이에요!!"], delimiter = ':')
    @DisplayName("repository를 이용해서 find 테스트. 동시에 시간 측정(Success)")
    fun repositorySuccessFindReviewWithIdAndTitle(reviewId: String, reviewTitle: String) {
        val stopWatch = StopWatch()

        // when
        stopWatch.start()
        val foundReview = shopReviewDynamoRepository.findReviewByIdAndTitle(reviewId, reviewTitle)
        stopWatch.stop()

        // then
        with(foundReview) {
            assertNotNull(this)
            assertEquals(this!!.reviewId, reviewId)
            assertEquals(this!!.reviewTitle, reviewTitle)
        }

        println("걸린 시간: ${stopWatch.totalTimeSeconds}")
        println(foundReview)
        println("Test passed!!")
    }

    @ParameterizedTest
    @CsvSource(value = ["가짜아이디:대만족이에요!"], delimiter = ':')
    @DisplayName("repository를 이용해서 find 테스트. reviewId를 잘못 주는 케이스(Fail)")
    fun repositoryFailureFindReviewWithIdAndTitle1(reviewId: String, reviewTitle: String) {
        // when
        val foundReview = shopReviewDynamoRepository.findReviewByIdAndTitle(reviewId, reviewTitle)

        with(foundReview) {
            assertNull(this)
        }

        println("Test passed!!")
    }

    @ParameterizedTest
    @CsvSource(value = ["6274cfba-92a7-4e44-9a8f-123113e928bc:불만족이에요!"], delimiter = ':')
    @DisplayName("repository를 이용해서 find 테스트. reviewTitle를 잘못 주는 케이스(Fail)")
    fun repositoryFailureFindReviewWithIdAndTitle2(reviewId: String, reviewTitle: String) {
        // when
        val foundReview = shopReviewDynamoRepository.findReviewByIdAndTitle(reviewId, reviewTitle)

        with(foundReview) {
            assertNull(this)
        }

        println("Test passed!!")
    }

    // ============================== [find all] ==============================

    @Test
    @DisplayName("client를 이용해서 모든 review 목록을 가져온다")
    fun clientFindAllReviews() {
        val reviewList = table.scan().items().toList()

        println(reviewList)
    }

    // ============================== [findReviewsByShop] ==============================

    @ParameterizedTest
    @CsvSource(value = ["33daf043-7f36-4a52-b791-018f9d5eb218:역전할머니맥주 영남대점"], delimiter = ':')
    @DisplayName("client를 이용해서 Shop에 대한 리뷰의 목록을 가져오는 로직 테스트.(Success)")
    fun clientReviewListByShopGsi1(shopId: String, shopName: String) {

        // given
        val stopWatch = StopWatch()
        val attributeAliasMap = mutableMapOf<String, String>()
        attributeAliasMap["#shop_id"] = "shop_id"
        attributeAliasMap["#shop_name"] = "shop_name"

        val attributeValueMap = mutableMapOf<String, AttributeValue>()
        attributeValueMap[":id_val"] = AttributeValue.fromS(shopId)
        attributeValueMap[":name_val"] = AttributeValue.fromS(shopName)

        // when
        val expression = Expression.builder()
            .expressionNames(attributeAliasMap)
            .expressionValues(attributeValueMap)
            .expression("#shop_id = :id_val AND #shop_name = :name_val")
            .build()

        // then
        stopWatch.start()
        table.scan {
            it.filterExpression(expression)
        }.items().forEach { it -> println(it) }
        stopWatch.stop()

        println("걸린 시간: ${stopWatch.totalTimeSeconds}")
    }

    @ParameterizedTest
    @CsvSource(value = ["nullnullnull:뭐가 없는 가게"], delimiter = ':')
    @DisplayName("client를 이용해서 Shop에 대한 리뷰의 목록을 가져오는 로직 테스트.(count = 0)")
    fun clientReviewListByShopGsi2(shopId: String, shopName: String) {
        // given
        var count = 0

        val attributeAliasMap = mutableMapOf<String, String>()
        attributeAliasMap["#shop_id"] = "shop_id"
        attributeAliasMap["#shop_name"] = "shop_name"

        val attributeValueMap = mutableMapOf<String, AttributeValue>()
        attributeValueMap[":id_val"] = AttributeValue.fromS(shopId)
        attributeValueMap[":name_val"] = AttributeValue.fromS(shopName)

        // when
        val expression = Expression.builder()
            .expressionNames(attributeAliasMap)
            .expressionValues(attributeValueMap)
            .expression("#shop_id = :id_val AND #shop_name = :name_val")
            .build()

        table.scan {
            it.filterExpression(expression)
        }.items().forEach { it -> count++ }

        // then
        assertEquals(count, 0)
    }

    @ParameterizedTest
    @CsvSource(value = ["33daf043-7f36-4a52-b791-018f9d5eb218:역전할머니맥주 영남대점"], delimiter = ':')
    @DisplayName("repository를 이용해서 Shop에 대한 리뷰의 목록을 가져오는 로직 테스트.(Success)")
    fun repositoryReviewListByShopGsi1(shopId: String, shopName: String) {
        val reviewList = shopReviewDynamoRepository.getReviewListByShopGsi(shopId, shopName)

        assertNotEquals(reviewList.size, 0)

        reviewList.forEach { it -> println(it) }
        println("Test passed!!")
    }

    @ParameterizedTest
    @CsvSource(value = ["nullnullnull:뭐가 없는 가게"], delimiter = ':')
    @DisplayName("repository를 이용해서 Shop에 대한 리뷰의 목록을 가져오는 로직 테스트.(reviewList.size = 0)")
    fun repositoryReviewListByShopGsi2(shopId: String, shopName: String) {
        val reviewList = shopReviewDynamoRepository.getReviewListByShopGsi(shopId, shopName)

        assertEquals(reviewList.size, 0)

        println("Test passed!!")
    }

    // ============================== [deleteReview] ==============================

    @ParameterizedTest
    @CsvSource(value = ["c4633cf7-dca1-447c-907c-acb2f0db73df:킹킹만족!"], delimiter = ':')
    @DisplayName("repository를 이용해서 리뷰 삭제 (Success)")
    fun deleteReview(reviewId: String, reviewTitle: String) {
        // when
        shopReviewDynamoRepository.deleteReview(reviewId, reviewTitle)

        // then
        val deletedShop = shopReviewDynamoRepository.findReviewByIdAndTitle(reviewId, reviewTitle)

        assertNull(deletedShop)

        println("Test passed!!")
    }

    /* ==============================[Async Test]============================== */
    @ParameterizedTest
    @CsvSource(value = ["xxxxxx-5120-4ec2-ab92-ca6827428945:진짜 최애 맥주집이에요!!"], delimiter = ':')
    @DisplayName("[Repository] reviewId가 틀려서 못 찾아오는 경우 테스트")
    fun findReviewByIdAndTitleFail1(reviewId: String, reviewTitle: String): Unit = runBlocking {
        // when
        val reviewMono = shopReviewDynamoRepository.findReviewByIdAndTitleAsync(reviewId, reviewTitle)
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
        val reviewMono = shopReviewDynamoRepository.findReviewByIdAndTitleAsync(reviewId, reviewTitle)
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
        val reviewMono = shopReviewDynamoRepository.findReviewByIdAndTitleAsync(reviewId, reviewTitle)
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
    @CsvSource(value = ["33daf043-7f36-4a52-b791-018f9d5eb218:역전할머니맥주 영남대점"], delimiter = ':')
    @DisplayName("[repository] review key들을 완전히 잘 가져오는지 테스트")
    fun testGetAllReviewKeyByShopIdAndName(shopId: String, shopName: String): Unit = runBlocking {
        val reviewKeysFlow = shopReviewDynamoRepository.getAllReviewKeyFlowByShopIdAndName(shopId, shopName)
        reviewKeysFlow.buffer()
            .collect {
                println("reviewId: ${it.first}, reviewTitle: ${it.second}")
            }
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
            reviewContent = "저는 아주 불만족했어요! ^^",
            reviewScore = 1.0,
            reviewPhotoList = listOf(),
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )
}