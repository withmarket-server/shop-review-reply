package team.bakkas.applicationcommand.handler

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.common.runBlocking
import io.mockk.*
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.mock.web.reactive.function.server.MockServerRequest
import reactor.core.publisher.Mono
import team.bakkas.applicationcommand.grpc.ifs.ShopGrpcClient
import team.bakkas.applicationcommand.validator.ShopValidatorImpl
import team.bakkas.clientcommand.shop.ShopCommand
import team.bakkas.common.exceptions.RegionNotKoreaException
import team.bakkas.common.exceptions.RequestBodyLostException
import team.bakkas.common.exceptions.RequestFieldException
import team.bakkas.common.exceptions.RequestParamLostException
import team.bakkas.common.exceptions.shop.ShopBranchInfoInvalidException
import team.bakkas.common.exceptions.shop.ShopNotFoundException
import team.bakkas.servicecommand.validator.ShopValidator
import team.bakkas.dynamo.shop.vo.DeliveryTipPerDistance
import team.bakkas.dynamo.shop.vo.category.Category
import team.bakkas.dynamo.shop.vo.category.DetailCategory
import team.bakkas.dynamo.shop.vo.sale.Days
import team.bakkas.eventinterface.eventProducer.ShopEventProducer
import team.bakkas.grpcIfs.v1.shop.CheckExistShopResponse
import java.time.LocalTime

@ExtendWith(MockKExtension::class)
internal class ShopCommandHandlerUnitTest {
    private lateinit var shopCommandHandler: ShopCommandHandler

    private lateinit var shopValidator: ShopValidator

    private lateinit var shopGrpcClient: ShopGrpcClient

    private lateinit var shopEventProducer: ShopEventProducer

    @BeforeEach
    fun setUp() {
        shopGrpcClient = mockk(relaxed = true)
        shopValidator = spyk(ShopValidatorImpl(shopGrpcClient)) // 실제 validator를 사용하기 위해 spyk로 선언
        shopEventProducer = mockk(relaxed = true)
        shopCommandHandler = spyk(ShopCommandHandler(shopValidator, shopEventProducer))
    }

    @Test
    @DisplayName("[createShop] 1. 위도/경도가 잘못되어 RegionNotKoreaException을 일으키는 테스트")
    fun createShopTest1(): Unit = runBlocking {
        // given
        val dto = generateCreateRequest().apply { latitude = 124.0 }
        val serverRequest = MockServerRequest.builder()
            .body(Mono.just(dto)) // ServerRequest는 Mono 타입이기 때문에 body도 Mono로 날려줘야한다

        // when
        shouldThrow<RegionNotKoreaException> { shopCommandHandler.createShop(serverRequest) }
    }

    @Test
    @DisplayName("[createShop] 2. Body가 날아오지 않아서 RequestBodyLostException을 일으키는 테스트")
    fun createShopTest2(): Unit = runBlocking {
        // given
        val serverRequest = MockServerRequest.builder()
            .body(Mono.empty<ShopCommand.CreateRequest>())

        // then
        shouldThrow<RequestBodyLostException> { shopCommandHandler.createShop(serverRequest) }
    }

    @Test
    @DisplayName("[createShop] 3. 본점인데 branchName이 있는 경우")
    fun createShopTest3(): Unit = runBlocking {
        // given
        val dto = generateCreateRequest().apply {
            isBranch = false
            branchName = "분점이에요 ㅎㅎ"
        }
        val requestBody = Mono.just(dto)
        val request = MockServerRequest.builder().body(requestBody)

        // then
        shouldThrow<ShopBranchInfoInvalidException> { shopCommandHandler.createShop(request) }
    }

    @Test
    @DisplayName("[createShop] 4. 분점인데 분점 정보가 없는 경우 (분점 정보가 empty)")
    fun createShopTest4(): Unit = runBlocking {
        // given
        val dto = generateCreateRequest().apply {
            isBranch = true
            branchName = ""
        }
        val requestBody = Mono.just(dto)
        val request = MockServerRequest.builder().body(requestBody)

        // then
        shouldThrow<ShopBranchInfoInvalidException> { shopCommandHandler.createShop(request) }
    }

    @Test
    @DisplayName("[createShop] 5. 분점인데 분점 정보가 없는 경우 (분점 정보가 null)")
    fun createShopTest5(): Unit = runBlocking {
        // given
        val dto = generateCreateRequest().apply {
            isBranch = true
            branchName = null
        }
        val requestBody = Mono.just(dto)
        val request = MockServerRequest.builder().body(requestBody)

        // then
        shouldThrow<ShopBranchInfoInvalidException> { shopCommandHandler.createShop(request) }
    }

    @Test
    @DisplayName("[createShop] 6. shopName이 비어서 들어오는 경우 RequestFieldException을 일으키는 테스트")
    fun createShopTest6(): Unit = runBlocking {
        // given
        val dto = generateCreateRequest().apply {
            shopName = ""
        }
        val requestBody = Mono.just(dto)
        val request = MockServerRequest.builder().body(requestBody)

        // then
        shouldThrow<RequestFieldException> { shopCommandHandler.createShop(request) }
    }

    @Test
    @DisplayName("[createShop] 7. 지번주소가 비어서 들어오는 경우 RequestFieldException을 일으키는 테스트")
    fun createShopTest7(): Unit = runBlocking {
        // given
        val dto = generateCreateRequest().apply {
            lotNumberAddress = ""
        }
        val requestBody = Mono.just(dto)
        val request = MockServerRequest.builder().body(requestBody)

        // then
        shouldThrow<RequestFieldException> { shopCommandHandler.createShop(request) }
    }

    @Test
    @DisplayName("[createShop] 8. 도로명주소가 비어서 들어오는 경우 RequestFieldException을 일으키는 테스트")
    fun createShopTest8(): Unit = runBlocking {
        // given
        val dto = generateCreateRequest().apply {
            roadNameAddress = ""
        }
        val requestBody = Mono.just(dto)
        val request = MockServerRequest.builder().body(requestBody)

        // then
        shouldThrow<RequestFieldException> { shopCommandHandler.createShop(request) }
    }

    @Test
    @DisplayName("[createShop] 9. memberId가 비어서 들어오는 경우 RequestFieldException을 일으키는 테스트")
    fun createShopTest9(): Unit = runBlocking {
        // given
        val dto = generateCreateRequest().apply {
            memberId = ""
        }
        val requestBody = Mono.just(dto)
        val request = MockServerRequest.builder()
            .body(requestBody)

        // then
        shouldThrow<RequestFieldException> { shopCommandHandler.createShop(request) }
    }

    @Test
    @DisplayName("[deleteShop] 1. shopId가 비어있는 문자열로 들어오는 경우 RequestParamLostException을 일으키는 테스트")
    fun deleteShopTest1(): Unit = runBlocking {
        // given
        val shopId = ""
        val shopName = "shop1"
        val request = MockServerRequest.builder()
            .queryParam("id", shopId)
            .queryParam("name", shopName)
            .build()

        // then
        shouldThrow<RequestParamLostException> { shopCommandHandler.deleteShop(request) }
    }

    @Test
    @DisplayName("[deleteShop] 4. shop이 존재하지 않는 경우 shopNotFoundException을 일으키는 테스트")
    fun deleteShopTest4(): Unit = runBlocking {
        // given
        val shopId = "1"
        val shopName = "shop1"
        val request = MockServerRequest.builder()
            .queryParam("id", shopId)
            .queryParam("name", shopName)
            .build()

        coEvery { shopGrpcClient.isExistShop(shopId) } returns
                CheckExistShopResponse.newBuilder()
                    .setResult(false)
                    .build()

        // then
        shouldThrow<ShopNotFoundException> { shopCommandHandler.deleteShop(request) }
    }

    @Test
    @DisplayName("[updateShop] 1. shop이 존재하지 않는 경우 예외를 일으키는 테스트")
    fun updateShopTest1(): Unit = runBlocking {
        // given
        val shopId = "qweqwe"
        val updateRequest = ShopCommand.UpdateRequest(
            shopId = shopId,
            shopName = "qwe",
            mainImage = null,
            representativeImageUrlList = null,
            openTimeRange = null,
            restDayList = null
        )
        val request = generateRequest { Mono.just(updateRequest) }

        coEvery { shopGrpcClient.isExistShop(shopId) } returns
                CheckExistShopResponse.newBuilder()
                    .setResult(false)
                    .build()

        // then
        shouldThrow<ShopNotFoundException> { shopCommandHandler.updateShop(request) }
    }

    // MockRequest를 생성해주는 메소드
    private inline fun generateRequest(block:() -> Mono<Any>): MockServerRequest {
        return MockServerRequest.builder()
            .body(block.invoke())
    }

    private fun generateCreateRequest(): ShopCommand.CreateRequest = ShopCommand.CreateRequest(
        shopName = "카페 경사다",
        openTime = LocalTime.of(9, 0),
        closeTime = LocalTime.of(18, 0),
        restDayList = listOf(Days.SAT),
        lotNumberAddress = "경산시 가짜동",
        roadNameAddress = "경산시 대학로",
        detailAddress = null,
        longitude = 128.7,
        latitude = 35.8,
        isBranch = false,
        shopDescription = "테스트용 가게입니다",
        shopCategory = Category.FOOD_BEVERAGE,
        shopDetailCategory = DetailCategory.CAFE_BREAD,
        mainImageUrl = "fake-image",
        representativeImageUrlList = listOf("fake-image-1", "fake-image-2"),
        deliveryTipPerDistanceList = listOf(DeliveryTipPerDistance(3.0, 2000)),
        memberId = "doccimann"
    )
}