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
import team.bakkas.domainquery.service.ifs.ShopQueryService
import team.bakkas.dynamo.shop.Shop
import team.bakkas.dynamo.shop.vo.*
import team.bakkas.dynamo.shop.vo.category.Category
import team.bakkas.dynamo.shop.vo.category.DetailCategory
import team.bakkas.dynamo.shop.vo.sale.Days
import team.bakkas.dynamo.shop.vo.sale.Status
import team.bakkas.grpcIfs.v1.shop.CheckExistShopRequest
import java.time.LocalDateTime
import java.time.LocalTime

// gRPC shop service에 대한 단위테스트
@ExtendWith(MockKExtension::class)
internal class GrpcShopServiceUnitTest {
    private lateinit var shopQueryService: ShopQueryService

    private lateinit var grpcShopService: GrpcShopService

    @BeforeEach
    fun setUp() {
        shopQueryService = mockk(relaxed = true)
        grpcShopService = spyk(GrpcShopService(shopQueryService))
    }

    @Test
    @DisplayName("[isExistShop] 1. Shop이 존재하지 않는 경우")
    fun isExistShopTest1(): Unit = runBlocking {
        // given
        val shopId = "shopId"
        val request = CheckExistShopRequest.newBuilder()
            .setShopId(shopId)
            .build()

        coEvery { shopQueryService.findShopById(shopId) } returns null

        // when
        val response = grpcShopService.isExistShop(request)

        // then
        coVerify(exactly = 1) { grpcShopService.isExistShop(request) }
        coVerify(exactly = 1) { shopQueryService.findShopById(shopId) }
        assertEquals(response.result, false)
    }

    @Test
    @DisplayName("[isExistShop] 2. Shop이 존재하는 경우")
    fun isExistShopTest2(): Unit = runBlocking {
        // given
        val shopId = "shopId"
        val shopName = "shopName"
        val request = CheckExistShopRequest.newBuilder()
            .setShopId(shopId)
            .build()

        coEvery { shopQueryService.findShopById(shopId) } returns
                generateFakeShop(shopId, shopName)

        // when
        val response = grpcShopService.isExistShop(request)

        // then
        coVerify(exactly = 1) { grpcShopService.isExistShop(request) }
        coVerify(exactly = 1) { shopQueryService.findShopById(shopId) }
        with(response) {
            assertEquals(this.result, true)
        }
    }

    @Test
    @DisplayName("[isExistShop] 3. Shop이 삭제되어있는 경우")
    fun findForDeletedShop(): Unit = runBlocking {
        // given
        val shopId = "shopId"
        val shopName = "shopName"
        val request = CheckExistShopRequest.newBuilder()
            .setShopId(shopId)
            .build()

        coEvery { shopQueryService.findShopById(shopId) } returns
                generateFakeShop(shopId, shopName).apply { deletedAt = LocalDateTime.now() }

        // when
        val response = grpcShopService.isExistShop(request)

        // then
        coVerify(exactly = 1) { grpcShopService.isExistShop(request) }
        coVerify(exactly = 1) { shopQueryService.findShopById(shopId) }
        with(response) {
            assertEquals(this.result, false)
        }
    }

    // 테스트용 가짜 shop
    private fun generateFakeShop(shopId: String, shopName: String) = Shop(
        shopId = shopId,
        shopName = shopName,
        salesInfo = SalesInfo(
            status = Status.OPEN,
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