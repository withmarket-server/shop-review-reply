package team.bakkas.domainqueryservice.service

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import team.bakkas.clientmobilequery.dto.ShopReviewBasicReadDto
import team.bakkas.common.exceptions.RequestParamLostException
import team.bakkas.common.exceptions.ShopReviewNotFoundException
import team.bakkas.domaindynamo.entity.ShopReview
import team.bakkas.domaindynamo.repository.ShopReviewRepository
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
internal class ShopReviewQueryServiceTest {
    @MockK
    private lateinit var shopReviewRepository: ShopReviewRepository

    // ============================== [find One using repository] ==============================

    @Test
    @DisplayName("review의 id와 review의 title을 이용해서 해당하는 리뷰를 하나 성공적으로 가져온다")
    fun findReviewByIdAndTitle1() {
        /** given */
        val reviewId = "review-id-1"
        val reviewTitle = "테스트용 리뷰 제목"

        every { shopReviewRepository.findReviewByIdAndTitle(reviewId, reviewTitle) } returns getMockReview(
            reviewId,
            reviewTitle,
            "가게-id-1",
            "가게1"
        )

        /** when */
        val foundReview = shopReviewRepository.findReviewByIdAndTitle(reviewId, reviewTitle)

        /** then */
        // reviewRepository의 findReviewByIdAndTitle이 한 번만 호출되었는가?
        verify(exactly = 1) { shopReviewRepository.findReviewByIdAndTitle(reviewId, reviewTitle) }
        // review의 결과 검증
        assertNotNull(foundReview)

        with(foundReview!!) {
            assertEquals(this.reviewId, reviewId)
            assertEquals(this.reviewTitle, reviewTitle)
        }

        println("Test passed!!")
    }

    @Test
    @DisplayName("요구 데이터를 모두 넣었으나, 데이터가 날아오지 않으면 Exception을 뱉어준다")
    fun findReviewByIdAndTitle2() {
        /** given */
        val reviewId = "review-id-1"
        val reviewTitle = "테스트용 리뷰 제목"

        // reviewid, reviewTitle을 넣어주었지만 네트워크 사정으로 null이 반환된 경우
        every { shopReviewRepository.findReviewByIdAndTitle(reviewId, reviewTitle) } returns null

        // when
        val foundReview = shopReviewRepository.findReviewByIdAndTitle(reviewId, reviewTitle)

        // then
        verify(exactly = 1) { shopReviewRepository.findReviewByIdAndTitle(reviewId, reviewTitle) }

        assertNull(foundReview)

        println("Test passed!!")
    }

    // ============================== [find One using service] ==============================

    @Test
    @DisplayName("reviewId, reviewTitle이 유실된 경우 exception을 검증하는 테스트 코드")
    fun findReviewByIdAndTitle3() {
        // Mock 객체 선언
        val service = ShopReviewQueryService(shopReviewRepository)

        // when
        try {
            val foundReview = service.findReviewByIdAndName(null, "제목은 있다!")
        } catch (e: RequestParamLostException) {
            // then
            assertEquals(e.message, "잘못된 형식의 검색. reviewId 혹은 reviewTitle을 확인하십시오.")
            println("Test passed!!")
        }
    }

    @Test
    @DisplayName("reviewId, reviewTitle이 빈 껍데기로 들어온 경우 exception을 검증하는 테스트 코드")
    fun findReviewByIdAndTitle4() {
        // Mock 객체 선언
        val service = ShopReviewQueryService(shopReviewRepository)

        // when
        try {
            val foundReview = service.findReviewByIdAndName("", "제목은 있다!")
        } catch (e: RequestParamLostException) {
            // then
            assertEquals(e.message, "잘못된 형식의 검색. reviewId 혹은 reviewTitle을 확인하십시오.")
            println("Test passed!!")
        }
    }

    @Test
    @DisplayName("reviewid, reviewTitle은 정상적으로 들어갔지만, 해당하는 entity가 없어서 exception이 터지는 경우")
    fun findReviewByIdAndTitle5() {
        // given
        val service = ShopReviewQueryService(shopReviewRepository)
        val reviewId = "review-id-1"
        val reviewTitle = "가짜 리뷰"

        every { shopReviewRepository.findReviewByIdAndTitle(reviewId, reviewTitle) } returns null

        // when
        try {
            // shopReviewRepository.findReviewByIdAndTitle이 null을 반환하기 때문에 exception이 발생
            val result = service.findReviewByIdAndName(reviewId, reviewTitle)
        } catch (e: ShopReviewNotFoundException) {
            // then
            assertEquals("(reviewId = $reviewId, reviewTitle = $reviewTitle)에 해당하는 review는 존재하지 않습니다.", e.message)
            println("Test passed!!")
        }
    }

    @Test
    @DisplayName("service가 모든 예외처리를 뚫고 정상 작동하는 경우")
    fun findReviewByIdAndTitle6() {
        val service = ShopReviewQueryService(shopReviewRepository)
        val reviewId = "review-id-1"
        val reviewTitle = "가짜 리뷰"

        every { shopReviewRepository.findReviewByIdAndTitle(reviewId, reviewTitle) } returns getMockReview(
            reviewId, reviewTitle, "shop-id-1", "가게1"
        )

        // when
        val result = service.findReviewByIdAndName(reviewId, reviewTitle)
        val resultBody: ShopReviewBasicReadDto = result.body!!.data

        // then
        verify(exactly = 1) { shopReviewRepository.findReviewByIdAndTitle(reviewId, reviewTitle) }
        assertNotNull(resultBody)
        with(resultBody) {
            assertEquals(this.reviewId, reviewId)
            assertEquals(this.reviewTitle, reviewTitle)
        }

        println(resultBody)
        println("Test passed!!")
    }

    // ============================== [find List using repository] ==============================

    @Test
    @DisplayName("service logic을 통과하는 모범적인 케이스 테스트")
    fun getShopReviewListByShopKey1() {
        // given
        // shop의 GSI 정보가 null이 아니다
        val shopId = "shop-id-1"
        val shopName = "가게1"
        val size = 10

        every { shopReviewRepository.getReviewListByShopGsi(shopId, shopName) } returns getMockShopReviewList(
            size,
            shopId,
            shopName
        )

        // when
        val resultList = shopReviewRepository.getReviewListByShopGsi(shopId, shopName)

        // then
        assertNotNull(shopId)
        assertNotNull(shopName)
        assertNotEquals(shopId, "")
        assertNotEquals(shopName, "")

        assertEquals(resultList.isEmpty(), false) // 리스트의 사이즈가 0은 아니다

        // resultList의 모든 review들이 같은 shop을 지향한다
        resultList.forEach { review ->
            assertEquals(review.shopId, shopId)
            assertEquals(review.shopName, shopName)
        }

        println("Test passed!!")
    }

    @Test
    @DisplayName("shopId, shopName은 초기 검증이 완료되었지만, 해당하는 리뷰가 없어서 사이즈가 0인 경우")
    fun getShopReviewListByShopKey2() {
        // given
        val shopId = "shop-id-1"
        val shopName = "가게1"

        every { shopReviewRepository.getReviewListByShopGsi(shopId, shopName) } returns listOf()

        // when
        val resultList = shopReviewRepository.getReviewListByShopGsi(shopId, shopName)

        // then
        verify(exactly = 1) { shopReviewRepository.getReviewListByShopGsi(shopId, shopName) }
        assertNotNull(shopId)
        assertNotNull(shopName)
        assertNotEquals(shopId, "")
        assertNotEquals(shopName, "")
        assertEquals(resultList.isEmpty(), true)

        println("Test passed!!")
    }

    @Test
    @DisplayName("list의 size가 0이 아니긴하지만, 이상한 데이터가 숨겨진 경우")
    fun getShopReviewListByShopKey3() {
        // given
        val shopId = "shop-id-1"
        val shopName = "가게1"
        val size = 10
        val wrongIndex = 1

        // 중간에 이상한 데이터를 끼운 리스트를 반환하게 설정
        every { shopReviewRepository.getReviewListByShopGsi(shopId, shopName) } returns getWrongMockShopReviewList(
            size, wrongIndex, shopId, shopName
        )

        // when
        val resultList = shopReviewRepository.getReviewListByShopGsi(shopId, shopName)

        // then
        verify(exactly = 1) { shopReviewRepository.getReviewListByShopGsi(shopId, shopName) }
        assertNotNull(shopId)
        assertNotNull(shopName)
        assertNotEquals(shopId, "")
        assertNotEquals(shopName, "")
        assertNotEquals(resultList.isEmpty(), true)

        resultList.forEachIndexed { i, review ->

            if (i == wrongIndex) {
                assertNotEquals(review.shopId, shopId)
                assertNotEquals(review.shopName, shopName)
            }
            else {
                assertEquals(review.shopId, shopId)
                assertEquals(review.shopName, shopName)
            }
        }

        println("Test passed!!")
    }

    // ============================== [find List using service] ==============================

    @Test
    @DisplayName("service logic 코드를 통과하는 모범적인 사례")
    fun getShopReviewListByShopKey4() {

    }


    // shopReview에 대한 가짜 객체 정의
    private fun getMockReview(reviewId: String, reviewTitle: String, shopId: String, shopName: String) = ShopReview(
        reviewId = reviewId,
        reviewTitle = reviewTitle,
        shopId = shopId,
        shopName = shopName,
        reviewContent = "테스트 리뷰에요!",
        reviewPhotoList = listOf("링크1", "링크2"),
        createdAt = LocalDateTime.now(),
        updatedAt = null,
        reviewScore = 10.0
    )

    // 특정 shop에 대한 shopReview의 리스트를 가져오는 메소드
    private fun getMockShopReviewList(size: Int, shopId: String, shopName: String): List<ShopReview> {
        val resultList = mutableListOf<ShopReview>()

        for (i in 0 until size)
            resultList.add(getMockReview("review-id-$i", "리뷰$i", shopId, shopName))

        return resultList
    }

    // 특정 index에 이상한 데이터가 끼워진 shopReview의 목록을 반환하는 메소드
    private fun getWrongMockShopReviewList(
        size: Int,
        wrongIndex: Int,
        shopId: String,
        shopName: String
    ): List<ShopReview> {
        val resultList = mutableListOf<ShopReview>()

        for (i in 0 until size) {

            if (i == wrongIndex)
                resultList.add(getMockReview("review-id-$i", "리뷰$i", "잘못된 id", "잘못된 가게"))
            else
                resultList.add(getMockReview("review-id-$i", "리뷰$i", shopId, shopName))
        }

        return resultList
    }
}