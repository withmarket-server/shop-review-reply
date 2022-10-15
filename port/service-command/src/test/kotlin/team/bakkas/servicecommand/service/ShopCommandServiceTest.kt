package team.bakkas.servicecommand.service

import io.kotest.common.runBlocking
import io.mockk.coVerify
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.spyk
import org.junit.jupiter.api.Assertions
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
import team.bakkas.repository.ifs.dynamo.ShopDynamoRepository
import team.bakkas.repository.ifs.redis.ShopRedisRepository
import java.time.LocalTime
import java.util.*

@ExtendWith(MockKExtension::class)
internal class ShopCommandServiceTest {

    // shopDynamoRepository에 대한 mock 객체
    private lateinit var shopDynamoRepository: ShopDynamoRepository

    // shopRedisRepository에 대한 mock 객체
    private lateinit var shopRedisRepository: ShopRedisRepository

    // 실제 객체들을 활용하기 위해 spyk로 선언할 객체들
    private lateinit var shopCommandService: ShopCommandService

    @BeforeEach
    fun setUp() {
        shopDynamoRepository = mockk()
        shopRedisRepository = mockk()
        shopCommandService = spyk(ShopCommandServiceImpl(shopDynamoRepository, shopRedisRepository)) // shopCommandService를 spyK mock으로 선언
    }

    @Test
    @DisplayName("[create shop] 성공 케이스 테스트")
    fun failCreateShopTest3(): Unit = runBlocking {
        // given
        val mockShopDto = generateDto()
        val mockShop = mockShopDto.toEntity()
        // Mono<Void>는 Mockk 테스트가 진행되지 않기 때문에 Mono.empty로 스터빙
        every { shopDynamoRepository.createShop(mockShop) }.returns(Mono.empty())

        // when
        val createShop = shopCommandService.createShop(mockShop)

        // then
        coVerify(exactly = 1) { shopCommandService.createShop(mockShop) }
        Assertions.assertNotNull(createShop)

        println("Test passed!!")
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
        salesInfo = SalesInfo(isOpen = false, openTime = openTime, closeTime = closeTime, restDayList = restDayList),
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