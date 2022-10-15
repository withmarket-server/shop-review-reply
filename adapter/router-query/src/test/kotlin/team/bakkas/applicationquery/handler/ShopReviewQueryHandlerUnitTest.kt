package team.bakkas.applicationquery.handler

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.common.runBlocking
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.spyk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.mock.web.reactive.function.server.MockServerRequest
import team.bakkas.common.exceptions.RequestParamLostException
import team.bakkas.common.exceptions.shopReview.ShopReviewNotFoundException
import team.bakkas.domainquery.service.ifs.ShopReviewQueryService
import team.bakkas.dynamo.shopReview.ShopReview
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
internal class ShopReviewQueryHandlerUnitTest {
    private lateinit var shopReviewHandler: ShopReviewQueryHandler

    private lateinit var shopReviewService: ShopReviewQueryService

    @BeforeEach
    fun setUp() {
        shopReviewService = mockk(relaxed = true)
        shopReviewHandler = spyk(ShopReviewQueryHandler(shopReviewService))
    }

    @Test
    @DisplayName("[findReviewByIdAndTitle] 1. reviewId가 유실되어 RequestParamLostException을 일으키는 테스트")
    fun findReviewByIdAndTitleTest1(): Unit = runBlocking {
        // given
        val reviewId = ""
        val reviewTitle = "review title"
        val request = MockServerRequest.builder()
            .queryParam("id", reviewId)
            .queryParam("title", reviewTitle)
            .build()

        // then
        shouldThrow<RequestParamLostException> { shopReviewHandler.findReviewByIdAndTitle(request) }
    }

    @Test
    @DisplayName("[findReviewByIdAndTitle] 2. reviewTitle이 유실되어 RequestParamLostException을 일으키는 테스트")
    fun findReviewByIdAndTitleTest2(): Unit = runBlocking {
        // given
        val reviewId = "review id"
        val reviewTitle = ""
        val request = MockServerRequest.builder()
            .queryParam("id", reviewId)
            .queryParam("title", reviewTitle)
            .build()

        // then
        shouldThrow<RequestParamLostException> { shopReviewHandler.findReviewByIdAndTitle(request) }
    }

    @Test
    @DisplayName("[findReviewByIdAndTitle] 3. reviewId와 reviewTitle 모두 유실되어 RequestParamLostException을 일으키는 테스트")
    fun findReviewByIdAndTitleTest3(): Unit = runBlocking {
        // given
        val reviewId = ""
        val reviewTitle = ""
        val request = MockServerRequest.builder()
            .queryParam("id", reviewId)
            .queryParam("title", reviewTitle)
            .build()

        // then
        shouldThrow<RequestParamLostException> { shopReviewHandler.findReviewByIdAndTitle(request) }
    }

    @Test
    @DisplayName("[findReviewByIdAndTitle] 4. reviewId, reviewTitle에 매칭되는 review가 존재하지 않아 ShopReviewNotFoundException을 일으키는 테스트")
    fun findReviewByIdAndTitleTest4(): Unit = runBlocking {
        // given
        val reviewId = "fake id"
        val reviewTitle = "fake title"
        val request = MockServerRequest.builder()
            .queryParam("id", reviewId)
            .queryParam("title", reviewTitle)
            .build()

        coEvery { shopReviewService.findReviewByIdAndTitle(reviewId, reviewTitle) } returns null

        // then
        shouldThrow<ShopReviewNotFoundException> { shopReviewHandler.findReviewByIdAndTitle(request) }
    }

    @Test
    @DisplayName("[findReviewByIdAndTitle] 5. 성공 테스트")
    fun findReviewByIdAndTitleSuccess(): Unit = runBlocking {
        // given
        val reviewId = "fake id"
        val reviewTitle = "fake title"
        val request = MockServerRequest.builder()
            .queryParam("id", reviewId)
            .queryParam("title", reviewTitle)
            .build()

        coEvery { shopReviewService.findReviewByIdAndTitle(reviewId, reviewTitle) } returns
                getMockReview(reviewId, reviewTitle, "shop id", "shop name")

        // when
        val result = shopReviewHandler.findReviewByIdAndTitle(request)

        // then
        coVerify(exactly = 1) { shopReviewService.findReviewByIdAndTitle(reviewId, reviewTitle) }
        coVerify(exactly = 1) { shopReviewHandler.findReviewByIdAndTitle(request) }
        assertEquals(result.statusCode(), HttpStatus.OK)
    }

    // 가짜 review를 반환하는 메소드
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