package team.bakkas.servicecommand.service

import io.kotest.common.runBlocking
import io.mockk.*
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Mono
import team.bakkas.clientcommand.shop.ShopCommand
import team.bakkas.dynamo.shop.Shop
import team.bakkas.servicecommand.service.ifs.ShopCommandService
import team.bakkas.dynamo.shop.vo.*
import team.bakkas.dynamo.shop.vo.category.Category
import team.bakkas.dynamo.shop.vo.category.DetailCategory
import team.bakkas.dynamo.shop.vo.sale.Days
import team.bakkas.dynamo.shop.vo.sale.Status
import team.bakkas.repository.ifs.dynamo.ShopDynamoRepository
import team.bakkas.repository.ifs.redis.ShopRedisRepository
import java.time.LocalTime
import java.util.*

@ExtendWith(MockKExtension::class)
internal class ShopCommandServiceUnitTest {

    // shopDynamoRepository에 대한 mock 객체
    private lateinit var shopDynamoRepository: ShopDynamoRepository

    // shopRedisRepository에 대한 mock 객체
    private lateinit var shopRedisRepository: ShopRedisRepository

    // 실제 객체들을 활용하기 위해 spyk로 선언할 객체들
    private lateinit var shopCommandService: ShopCommandService

    @BeforeEach
    fun setUp() {
        shopDynamoRepository = mockk(relaxed = true)
        shopRedisRepository = mockk(relaxed = true)
        shopCommandService = spyk(
            ShopCommandServiceImpl(shopDynamoRepository, shopRedisRepository)
        )
    }

    @Test
    @DisplayName("[create shop] 성공 케이스 테스트")
    fun createTestSuccess(): Unit = runBlocking {
        // given
        val mockShopDto = generateDto()
        val mockShop = mockShopDto.toEntity()

        every { shopDynamoRepository.createShop(mockShop) } returns
                Mono.just(mockShop)

        // when
        val createShop = shopCommandService.createShop(mockShop).awaitSingleOrNull()

        // then
        coVerify(exactly = 1) { shopDynamoRepository.createShop(mockShop) }
        coVerify(exactly = 1) { shopCommandService.createShop(mockShop) }
        Assertions.assertNotNull(createShop)

        println("Test passed!!")
    }

    @Test
    @DisplayName("[apply create review] 성공 케이스 테스트")
    fun applyCreateReviewTest(): Unit = runBlocking {
        // given
        val mockShop = generateDto().toEntity()
        val shopId = mockShop.shopId
        val shopName = mockShop.shopName
        val reviewScore = 5.0

        every { shopDynamoRepository.findShopById(shopId) } returns Mono.just(mockShop)
        every { shopDynamoRepository.createShop(mockShop) } returns Mono.just(mockShop)

        // when
        val result = shopCommandService.applyCreateReview(shopId, reviewScore).awaitSingleOrNull()

        // then
        verify(exactly = 1) { shopCommandService.applyCreateReview(shopId, reviewScore) }
        verify(exactly = 1) { shopDynamoRepository.findShopById(shopId) }
        verify(exactly = 1) { shopDynamoRepository.createShop(mockShop) }
        verify(exactly = 1) { shopRedisRepository.cacheShop(mockShop) }
        assertNotNull(result)
        result?.let {
            assertEquals(it.reviewNumber, 1)
            assertEquals(it.totalScore, reviewScore)
        }
    }

    @Test
    @DisplayName("[apply delete review] 성공 케이스 테스트")
    fun applyDeleteReviewTest(): Unit = runBlocking {
        // given
        val reviewScore = 5.0
        val mockShop = generateDto().toEntity().apply {
            this.reviewNumber = 1
            this.totalScore = reviewScore
        }
        val shopId = mockShop.shopId
        val shopName = mockShop.shopName

        every { shopDynamoRepository.findShopById(shopId) } returns Mono.just(mockShop)
        every { shopDynamoRepository.createShop(mockShop) } returns Mono.just(mockShop)

        // when
        val result = shopCommandService.applyDeleteReview(shopId, reviewScore).awaitSingleOrNull()

        // then
        verify(exactly = 1) { shopCommandService.applyDeleteReview(shopId, reviewScore) }
        verify(exactly = 1) { shopDynamoRepository.findShopById(shopId) }
        verify(exactly = 1) { shopDynamoRepository.createShop(mockShop) }
        verify(exactly = 1) { shopRedisRepository.cacheShop(mockShop) }
        assertNotNull(result)
        result?.let {
            assertEquals(it.reviewNumber, 0)
            assertEquals(it.totalScore, 0.0)
        }
    }

    // create test용 dto를 생성해내는 메소드
    private fun generateDto(): ShopCommand.CreateRequest = ShopCommand.CreateRequest(
        shopName = "카페 경사다",
        openTime = LocalTime.of(9, 0),
        closeTime = LocalTime.of(18, 0),
        restDayList = listOf(Days.SAT),
        lotNumberAddress = "경산시 가짜동",
        roadNameAddress = "경산시 대학로",
        detailAddress = null,
        latitude = 128.7,
        longitude = 35.8,
        isBranch = false,
        shopDescription = "테스트용 가게입니다",
        shopCategory = Category.FOOD_BEVERAGE,
        shopDetailCategory = DetailCategory.CAFE_BREAD,
        mainImageUrl = "fake-image",
        representativeImageUrlList = listOf("fake-image-1", "fake-image-2"),
        deliveryTipPerDistanceList = listOf(DeliveryTipPerDistance(3.0, 2000))
    )

    private fun ShopCommand.CreateRequest.toEntity() = Shop(
        shopId = UUID.randomUUID().toString(),
        shopName = shopName,
        salesInfo = SalesInfo(status = Status.CLOSE, openTime = openTime, closeTime = closeTime, restDayList = restDayList),
        addressInfo = AddressInfo(lotNumberAddress, roadNameAddress),
        latLon = LatLon(latitude, longitude),
        shopImageInfo = ShopImageInfo(mainImageUrl, representativeImageUrlList),
        branchInfo = BranchInfo(isBranch, branchName),
        categoryInfo = CategoryInfo(shopCategory, shopDetailCategory),
        deliveryTipPerDistanceList = deliveryTipPerDistanceList,
        totalScore = 0.0,
        reviewNumber = 0,
        shopDescription = shopDescription
    )
}