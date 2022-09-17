package team.bakkas.applicationcommand.handler

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.common.runBlocking
import io.mockk.*
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.mock.web.reactive.function.server.MockServerRequest
import reactor.core.Disposables
import reactor.core.publisher.Mono
import team.bakkas.applicationcommand.extensions.toEntity
import team.bakkas.applicationcommand.validator.ShopValidatorImpl
import team.bakkas.clientcommand.dto.ShopCommand
import team.bakkas.common.category.Category
import team.bakkas.common.category.DetailCategory
import team.bakkas.common.exceptions.RegionNotKoreaException
import team.bakkas.common.exceptions.RequestBodyLostException
import team.bakkas.common.exceptions.RequestFieldException
import team.bakkas.common.exceptions.shop.ShopBranchInfoInvalidException
import team.bakkas.domaindynamo.entity.Shop
import team.bakkas.domainshopcommand.service.ShopCommandServiceImpl
import team.bakkas.domainshopcommand.service.ifs.ShopCommandService
import team.bakkas.domainshopcommand.validator.ShopValidator
import team.bakkas.eventinterface.eventProducer.ShopEventProducer
import team.bakkas.repository.ifs.dynamo.ShopDynamoRepository
import java.time.LocalTime

@ExtendWith(MockKExtension::class)
internal class ShopCommandHandlerUnitTest {
    private lateinit var shopCommandHandler: ShopCommandHandler

    private lateinit var shopCommandService: ShopCommandService

    private lateinit var shopDynamoRepository: ShopDynamoRepository

    private lateinit var shopValidator: ShopValidator

    private lateinit var shopEventProducer: ShopEventProducer

    @BeforeEach
    fun setUp() {
        shopDynamoRepository = mockk(relaxed = true)
        shopCommandService = spyk(ShopCommandServiceImpl(shopDynamoRepository))
        shopValidator = spyk(ShopValidatorImpl()) // 실제 validator를 사용하기 위해 spyk로 선언
        shopEventProducer = mockk()
        shopCommandHandler = spyk(ShopCommandHandler(shopCommandService, shopValidator, shopEventProducer))
    }

    @Test
    @DisplayName("[createShop] 1. 위도/경도가 잘못되어 RegionNotKoreaException을 일으키는 테스트")
    fun createShopTest1(): Unit = runBlocking {
        // given
        val dto = generateDto().apply { latitude = 124.0 }
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
        val dto = generateDto().apply {
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
        val dto = generateDto().apply {
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
        val dto = generateDto().apply {
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
        val dto = generateDto().apply {
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
        val dto = generateDto().apply {
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
        val dto = generateDto().apply {
            roadNameAddress = ""
        }
        val requestBody = Mono.just(dto)
        val request = MockServerRequest.builder().body(requestBody)

        // then
        shouldThrow<RequestFieldException> { shopCommandHandler.createShop(request) }
    }

    private fun generateDto(): ShopCommand.CreateRequest = ShopCommand.CreateRequest(
        shopName = "카페 경사다",
        openTime = LocalTime.of(9, 0),
        closeTime = LocalTime.of(18, 0),
        lotNumberAddress = "경산시 가짜동",
        roadNameAddress = "경산시 대학로",
        latitude = 128.7,
        longitude = 35.8,
        isBranch = false,
        shopDescription = "테스트용 가게입니다",
        shopCategory = Category.FOOD_BEVERAGE,
        shopDetailCategory = DetailCategory.CAFE_BREAD,
        mainImageUrl = "fake-image",
        representativeImageUrlList = listOf("fake-image-1", "fake-image-2")
    )
}