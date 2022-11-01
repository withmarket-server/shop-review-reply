package team.bakkas.applicationquery.handler

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.common.runBlocking
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.spyk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.mock.web.reactive.function.server.MockServerRequest
import reactor.core.publisher.Mono
import team.bakkas.common.exceptions.RequestParamLostException
import team.bakkas.common.exceptions.shop.ShopNotFoundException
import team.bakkas.domainquery.service.ifs.ShopQueryService
import team.bakkas.dynamo.shop.Shop
import team.bakkas.dynamo.shop.vo.*
import team.bakkas.dynamo.shop.vo.category.Category
import team.bakkas.dynamo.shop.vo.category.DetailCategory
import team.bakkas.dynamo.shop.vo.sale.Days
import team.bakkas.dynamo.shop.vo.sale.Status
import java.time.LocalTime

@ExtendWith(MockKExtension::class)
internal class ShopQueryHandlerUnitTest {
    private lateinit var shopQueryHandler: ShopQueryHandler

    private lateinit var shopQueryService: ShopQueryService

    @BeforeEach
    fun setUp() {
        shopQueryService = mockk(relaxed = true)
        shopQueryHandler = spyk(ShopQueryHandler(shopQueryService))
    }

    @Test
    @DisplayName("[findByIdAndName] 1. shopId가 비어서 들어온 경우 RequestParamLostException을 일으키는 테스트")
    fun findByIdAndNameTest1(): Unit = runBlocking {
        // given
        val shopId = ""
        val request = MockServerRequest.builder()
            .queryParam("id", shopId)
            .build()

        // then
        shouldThrow<RequestParamLostException> { shopQueryHandler.findById(request) }
    }

    @Test
    @DisplayName("[findById] 4. shopId에 대응하는 shop이 존재하지 않는 경우 ShopNotFoundException을 일으키는 테스트")
    fun findByIdAndNameTest4(): Unit = runBlocking {
        // given
        val shopId = "fake shop id"
        val request = MockServerRequest.builder()
            .queryParam("id", shopId)
            .build()

        // shop이 존재하지 않는다고 가정한다
        coEvery { shopQueryService.findShopById(shopId) } returns null

        // then
        shouldThrow<ShopNotFoundException> { shopQueryHandler.findById(request) }
    }

    @Test
    @DisplayName("[findByIdAndName] 5. 성공 테스트")
    fun findByIdAndNameSuccess(): Unit = runBlocking {
        // given
        val shopId = "fake shop id"
        val shopName = "fake shop name"
        val request = MockServerRequest.builder()
            .queryParam("id", shopId)
            .build()

        // shop이 존재하지 않는다고 가정한다
        coEvery { shopQueryService.findShopById(shopId) } returns generateFakeShop(shopId, shopName)

        // when
        val result = shopQueryHandler.findById(request)

        // then
        coVerify(exactly = 1) { shopQueryHandler.findById(request) } // queryHandler는 정확히 1회만 호출
        coVerify(exactly = 1) { shopQueryService.findShopById(shopId) } // queryService는 정확히 1회만 호출
        assertEquals(result.statusCode(), HttpStatus.OK) // OK response를 반환
    }

    @Test
    @DisplayName("[getAllShops] 1. shop이 하나도 없는 경우 shopNotFoundException을 일으키는 테스트")
    fun getAllShopsTest1(): Unit = runBlocking {
        // given
        val request = MockServerRequest.builder().build()

        // 비어있는 리스트를 반환하여 shop은 하나도 없다고 가정한다
        coEvery { shopQueryService.getAllShopList() } returns listOf()

        // then
        shouldThrow<ShopNotFoundException> { shopQueryHandler.getAllShops(request) }
    }

    @Test
    @DisplayName("[getAllShops] 2. 성공 테스트")
    fun getAllShopsTestSuccess(): Unit = runBlocking {
        // given
        val request = MockServerRequest.builder().build()

        val shopList = listOf(
            generateFakeShop("1", "first"),
            generateFakeShop("2", "second")
        )

        // Shop은 존재한다고 가정한다
        coEvery { shopQueryService.getAllShopList() } returns shopList

        // when
        val result = shopQueryHandler.getAllShops(request)

        // then
        coVerify(exactly = 1) { shopQueryHandler.getAllShops(request) }
        coVerify(exactly = 1) { shopQueryService.getAllShopList() }
        assertEquals(result.statusCode(), HttpStatus.OK)
    }

    // MockServerRequest를 생성하는 메소드
    private inline fun generateRequest(block: () -> Mono<Any>): MockServerRequest {
        return MockServerRequest.builder()
            .body(block())
    }

    // 테스트용 가짜 shop
    private fun generateFakeShop(shopId: String, shopName: String) = Shop(
        shopId = shopId,
        shopName = shopName,
        salesInfo = SalesInfo(
            status = Status.CLOSE,
            openTime = LocalTime.now(),
            closeTime = LocalTime.now(),
            restDayList = listOf(Days.SUN)
        ),
        addressInfo = AddressInfo("lotNumberAddress", "roadNameAddress"),
        latLon = LatLon(128.0, 36.0),
        shopImageInfo = ShopImageInfo("mainImageUrl", listOf("qwe")),
        branchInfo = BranchInfo(false, null),
        categoryInfo = CategoryInfo(Category.MART, DetailCategory.SUPER_MARKET),
        deliveryTipPerDistanceList = listOf(DeliveryTipPerDistance(3.0, 3000)),
        totalScore = 0.0,
        reviewNumber = 0,
        shopDescription = "shopDescription"
    )
}