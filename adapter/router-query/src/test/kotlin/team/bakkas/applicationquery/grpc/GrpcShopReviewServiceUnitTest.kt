package team.bakkas.applicationquery.grpc

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
import team.bakkas.domainquery.service.ifs.ShopReviewQueryService
import team.bakkas.dynamo.shopReview.ShopReview
import team.bakkas.grpcIfs.v1.shopReview.CheckExistShopReviewRequest
import java.time.LocalDateTime

// gRPC ShopReview Service에 대한 단위테스트
@ExtendWith(MockKExtension::class)
internal class GrpcShopReviewServiceUnitTest {
    private lateinit var shopReviewQueryService: ShopReviewQueryService

    private lateinit var grpcShopReviewService: GrpcShopReviewService

    @BeforeEach
    fun setUp() {
        shopReviewQueryService = mockk(relaxed = true)
        grpcShopReviewService = spyk(GrpcShopReviewService(shopReviewQueryService))
    }

    @Test
    @DisplayName("[isExistShopReview] Review가 존재하지 않는 경우")
    fun isExistShopReview1(): Unit = runBlocking {
        // given
        val reviewId = "1"
        val reviewTitle = "review1"
        val request = CheckExistShopReviewRequest.newBuilder()
            .setReviewId(reviewId)
            .setReviewTitle(reviewTitle)
            .build()

        coEvery { shopReviewQueryService.findReviewByIdAndTitle(reviewId, reviewTitle) } returns null

        // when
        val response = grpcShopReviewService.isExistShopReview(request)

        // then
        coVerify(exactly = 1) { grpcShopReviewService.isExistShopReview(request) }
        coVerify(exactly = 1) { shopReviewQueryService.findReviewByIdAndTitle(reviewId, reviewTitle) }
        assertEquals(response.result, false)
    }

    @Test
    @DisplayName("[isExistShopReview] Review가 존재하는 경우 성공")
    fun isExistShopReview2(): Unit = runBlocking {
        // given
        val reviewId = "1"
        val reviewTitle = "review1"
        val shopId = "1"
        val shopName = "shop1"
        val request = CheckExistShopReviewRequest.newBuilder()
            .setReviewId(reviewId)
            .setReviewTitle(reviewTitle)
            .build()

        coEvery { shopReviewQueryService.findReviewByIdAndTitle(reviewId, reviewTitle) } returns
                getMockReview(reviewId, reviewTitle, shopId, shopName)

        // when
        val response = grpcShopReviewService.isExistShopReview(request)

        // then
        coVerify(exactly = 1) { grpcShopReviewService.isExistShopReview(request) }
        coVerify(exactly = 1) { shopReviewQueryService.findReviewByIdAndTitle(reviewId, reviewTitle) }
        assertEquals(response.result, true)
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