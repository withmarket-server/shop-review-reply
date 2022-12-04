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
import team.bakkas.applicationquery.grpc.client.GrpcShopSearchClient
import team.bakkas.common.exceptions.RequestParamLostException
import team.bakkas.common.exceptions.shop.CategoryNotFoundException
import team.bakkas.common.exceptions.shop.DetailCategoryNotFoundException
import team.bakkas.common.exceptions.shop.ShopNotFoundException
import team.bakkas.domainquery.service.ifs.ShopQueryService
import team.bakkas.dynamo.shop.Shop
import team.bakkas.dynamo.shop.vo.*
import team.bakkas.dynamo.shop.vo.category.Category
import team.bakkas.dynamo.shop.vo.category.DetailCategory
import team.bakkas.dynamo.shop.vo.sale.Days
import team.bakkas.dynamo.shop.vo.sale.Status
import team.bakkas.shop.search.SearchResponse
import java.time.LocalTime

@ExtendWith(MockKExtension::class)
internal class ShopQueryHandlerUnitTest {
    private lateinit var shopQueryHandler: ShopQueryHandler

    private lateinit var shopQueryService: ShopQueryService

    private lateinit var grpcShopSearchClient: GrpcShopSearchClient

    @BeforeEach
    fun setUp() {
        shopQueryService = mockk(relaxed = true)
        grpcShopSearchClient = mockk(relaxed = true)
        shopQueryHandler = spyk(ShopQueryHandler(shopQueryService, grpcShopSearchClient))
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

    @Test
    @DisplayName("[searchByCategoryWithIn] 1. 존재하지도 않는 카테고리로 들어오는 경우 테스트")
    fun searchByCategoryWithInTest1(): Unit = runBlocking {
        // given
        val category = "FOOD_VEBERAGE"
        val latitude = "0.0"
        val longitude = "0.0"
        val distance = "0.0"
        val unit = "km"
        val page = "0"
        val size = "100"

        val request = MockServerRequest.builder()
            .queryParam("category", category)
            .queryParam("latitude", latitude)
            .queryParam("longitude", longitude)
            .queryParam("distance", distance)
            .queryParam("unit", unit)
            .queryParam("page", page)
            .queryParam("size", size)
            .build()

        // then
        shouldThrow<CategoryNotFoundException> { shopQueryHandler.searchByCategoryWithIn(request) }
    }

    @Test
    @DisplayName("[searchByCategoryWithIn] 2. shop이 하나도 존재하지 않는 경우")
    fun searchByCategoryWithInTest2(): Unit = runBlocking {
        // given
        val category = "FOOD_BEVERAGE"
        val latitude = "0.0"
        val longitude = "0.0"
        val distance = "0.0"
        val unit = "km"
        val page = "0"
        val size = "100"

        val request = MockServerRequest.builder()
            .queryParam("category", category)
            .queryParam("latitude", latitude)
            .queryParam("longitude", longitude)
            .queryParam("distance", distance)
            .queryParam("unit", unit)
            .queryParam("page", page)
            .queryParam("size", size)
            .build()

        // 비어있는 리스트를 반환
        coEvery {
            grpcShopSearchClient.searchCategoryWIthIn(
                category,
                latitude.toDouble(),
                longitude.toDouble(),
                distance.toDouble(),
                unit,
                page.toInt(),
                size.toInt()
            )
        } returns
                SearchResponse.newBuilder()
                    .addAllIds(listOf())
                    .build()

        // then
        shouldThrow<ShopNotFoundException> { shopQueryHandler.searchByCategoryWithIn(request) }
    }

    @Test
    @DisplayName("[searchByDetailCategoryWithIn] 1. 유효하지 않은 detail category")
    fun searchByDetailCategoryWithInTest1(): Unit = runBlocking {
        // given
        val detailCategory = "CAFE_BRAND"
        val latitude = "0.0"
        val longitude = "0.0"
        val distance = "0.0"
        val unit = "km"
        val page = "0"
        val size = "100"

        val request = MockServerRequest.builder()
            .queryParam("detail-category", detailCategory)
            .queryParam("latitude", latitude)
            .queryParam("longitude", longitude)
            .queryParam("distance", distance)
            .queryParam("unit", unit)
            .queryParam("page", page)
            .queryParam("size", size)
            .build()

        // then
        shouldThrow<DetailCategoryNotFoundException> { shopQueryHandler.searchByDetailCategoryWithIn(request) }
    }

    @Test
    @DisplayName("[searchByDetailCategoryWithIn] 2. detail category에 해당하는 shop이 존재하지 않는 경우")
    fun searchByDetailCategoryWithInTest2(): Unit = runBlocking {
        // given
        val detailCategory = "CAFE_BREAD"
        val latitude = "0.0"
        val longitude = "0.0"
        val distance = "0.0"
        val unit = "km"
        val page = "0"
        val size = "100"

        val request = MockServerRequest.builder()
            .queryParam("detail-category", detailCategory)
            .queryParam("latitude", latitude)
            .queryParam("longitude", longitude)
            .queryParam("distance", distance)
            .queryParam("unit", unit)
            .queryParam("page", page)
            .queryParam("size", size)
            .build()

        coEvery {
            grpcShopSearchClient.searchDetailCategoryWithIn(
                detailCategory,
                latitude.toDouble(),
                latitude.toDouble(),
                distance.toDouble(),
                unit,
                page.toInt(),
                size.toInt()
            )
        } returns
                SearchResponse.newBuilder()
                    .addAllIds(listOf())
                    .build()

        // then
        shouldThrow<ShopNotFoundException> { shopQueryHandler.searchByDetailCategoryWithIn(request) }
    }

    @Test
    @DisplayName("[searchByShopNameWithIn] 1. 두 글자 미만으로 검색을 시도하는 경우")
    fun searchByShopNameWithInTest1(): Unit = runBlocking {
        // given
        val shopName = "g"
        val latitude = "0.0"
        val longitude = "0.0"
        val distance = "0.0"
        val unit = "km"
        val page = "0"
        val size = "100"

        val request = MockServerRequest.builder()
            .queryParam("shop-name", shopName)
            .queryParam("latitude", latitude)
            .queryParam("longitude", longitude)
            .queryParam("distance", distance)
            .queryParam("unit", unit)
            .queryParam("page", page)
            .queryParam("size", size)
            .build()

        // then
        shouldThrow<RequestParamLostException> { shopQueryHandler.searchByShopNameWithIn(request) }
    }

    @Test
    @DisplayName("[searchByShopNameWithIn] 2. 가게가 존재하지 않는 경우")
    fun searchByShopNameWithInTest2(): Unit = runBlocking {
        // given
        val shopName = "롯데리아"
        val latitude = "0.0"
        val longitude = "0.0"
        val distance = "0.0"
        val unit = "km"
        val page = "0"
        val size = "100"

        val request = MockServerRequest.builder()
            .queryParam("shop-name", shopName)
            .queryParam("latitude", latitude)
            .queryParam("longitude", longitude)
            .queryParam("distance", distance)
            .queryParam("unit", unit)
            .queryParam("page", page)
            .queryParam("size", size)
            .build()

        coEvery {
            grpcShopSearchClient.searchShopNameWithIn(
                shopName,
                latitude.toDouble(),
                longitude.toDouble(),
                distance.toDouble(),
                unit,
                page.toInt(),
                size.toInt()
            )
        } returns
                SearchResponse.newBuilder()
                    .addAllIds(listOf())
                    .build()

        // then
        shouldThrow<ShopNotFoundException> { shopQueryHandler.searchByShopNameWithIn(request) }
    }

    @Test
    @DisplayName("[searchWithIn] 1. 반경 내에 가게가 없는 경우")
    fun searchWithInTest1(): Unit = runBlocking {
        // given
        val latitude = "0.0"
        val longitude = "0.0"
        val distance = "0.0"
        val unit = "km"
        val page = "0"
        val size = "100"

        val request = MockServerRequest.builder()
            .queryParam("latitude", latitude)
            .queryParam("longitude", longitude)
            .queryParam("distance", distance)
            .queryParam("unit", unit)
            .queryParam("page", page)
            .queryParam("size", size)
            .build()

        coEvery {
            grpcShopSearchClient.searchWithIn(
                latitude.toDouble(),
                longitude.toDouble(),
                distance.toDouble(),
                unit,
                page.toInt(),
                size.toInt()
            )
        } returns
                SearchResponse.newBuilder()
                    .addAllIds(listOf())
                    .build()

        // then
        shouldThrow<ShopNotFoundException> { shopQueryHandler.searchWithIn(request) }
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
        shopDescription = "shopDescription",
        memberId = "3333-3333-3333"
    )
}