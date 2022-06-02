package team.bakkas.domaindynamo.repository

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import team.bakkas.domaindynamo.entity.ShopReview
import java.util.*

@SpringBootTest
internal class ShopReviewRepositoryTest @Autowired constructor(
    private val dynamoDbEnhancedClient: DynamoDbEnhancedClient,
    private val shopReviewRepository: ShopReviewRepository
) {
    // 테이블 정의
    val table = dynamoDbEnhancedClient.table("shop_review", TableSchema.fromBean(ShopReview::class.java))

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
    @CsvSource(value = ["ecdc2-test001:대만족이에요!:33daf043-7f36-4a52-b791-018f9d5eb218:역전할머니맥주 영남대점"], delimiter = ':')
    @DisplayName("repository을 이용해서 데이터를 넣어보자!")
    @Rollback(value = false)
    fun createShopWithRepository(reviewId: String, reviewTitle: String, shopId: String, shopName: String) {
        // given
        val mockReview = getMockReview(UUID.randomUUID().toString(), reviewTitle, shopId, shopName)

        // when
        val createdReview = shopReviewRepository.createReview(mockReview)

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
    @CsvSource(value = ["6274cfba-92a7-4e44-9a8f-123113e928bc:대만족이에요!"], delimiter = ':')
    @DisplayName("repository를 이용해서 find 테스트. (Success)")
    fun repositorySuccessFindReviewWithIdAndTitle(reviewId: String, reviewTitle: String) {
        // when
        val foundReview = shopReviewRepository.findReviewByIdAndTitle(reviewId, reviewTitle)

        // then
        with(foundReview) {
            assertNotNull(this)
            assertEquals(this!!.reviewId, reviewId)
            assertEquals(this!!.reviewTitle, reviewTitle)
        }

        println(foundReview)
        println("Test passed!!")
    }

    @ParameterizedTest
    @CsvSource(value = ["가짜아이디:대만족이에요!"], delimiter = ':')
    @DisplayName("repository를 이용해서 find 테스트. reviewId를 잘못 주는 케이스(Fail)")
    fun repositoryFailureFindReviewWithIdAndTitle1(reviewId: String, reviewTitle: String) {
        // when
        val foundReview = shopReviewRepository.findReviewByIdAndTitle(reviewId, reviewTitle)

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
        val foundReview = shopReviewRepository.findReviewByIdAndTitle(reviewId, reviewTitle)

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

    // TODO delete test

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
            reviewContent = "저는 아주 만족했어요! ^^",
            reviewScore = 10.0,
            reviewPhotoList = listOf()
        )
}