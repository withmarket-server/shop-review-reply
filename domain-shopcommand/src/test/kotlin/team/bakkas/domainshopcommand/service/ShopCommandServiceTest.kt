package team.bakkas.domainshopcommand.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.common.runBlocking
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.spyk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Mono
import team.bakkas.clientcommand.dto.ShopCommand
import team.bakkas.common.category.Category
import team.bakkas.common.category.DetailCategory
import team.bakkas.common.exceptions.RegionNotKoreaException
import team.bakkas.common.exceptions.shop.ShopBranchInfoInvalidException
import team.bakkas.domaindynamo.repository.dynamo.ShopDynamoRepositoryImpl
import team.bakkas.domaindynamo.validator.ShopValidator
import team.bakkas.domainshopcommand.extensions.toEntity
import java.time.LocalTime

@ExtendWith(MockKExtension::class)
internal class ShopCommandServiceTest {

    @MockK(relaxed = true)
    private lateinit var shopDynamoRepository: ShopDynamoRepositoryImpl

    // 실제 객체들을 활용하기 위해 spyk로 선언할 객체들
    private lateinit var shopValidator: ShopValidator
    private lateinit var shopCommandService: ShopCommandServiceImpl

    @BeforeEach
    fun setUp() {
        shopValidator = spyk(ShopValidator())
        shopCommandService = spyk(ShopCommandServiceImpl(shopDynamoRepository, shopValidator)) // shopCommandService를 spyK mock으로 선언
    }

    // 좌표가 안 맞아서 에러가 터지는 경우 테스트
    @Test
    @DisplayName("[create shop] 1. 위도 경도가 우리나라가 아니라서 실패하는 케이스")
    fun failCreateShopTest1(): Unit = runBlocking {
        // given
        val fakeLatitude = 90.0
        val fakeLongitude = 34.0

        val mockShopDto = generateDto().apply {
            latitude = fakeLatitude
            longitude = fakeLongitude
        }
        val mockShop = mockShopDto.toEntity()

        every { shopDynamoRepository.createShopAsync(mockShop) } returns Mono.empty()

        // when
        val exception =
            shouldThrow<RegionNotKoreaException> { shopCommandService.createShop(mockShopDto) }

        // then
        coVerify(exactly = 1) { shopCommandService.createShop(mockShopDto) }
        assert(exception is RegionNotKoreaException)

        println("Test passed!!")
    }

    @Test
    @DisplayName("[create shop] 2. isBranch가 false인데 branchName이 null이 아닌 경우 실패 테스트")
    fun failCreateShopTest2(): Unit = runBlocking {
        // given
        val mockShopDto = generateDto().apply {
            isBranch = false
            branchName = "분점 정보"
        }

        // when
        val exception = shouldThrow<ShopBranchInfoInvalidException> {
            shopCommandService.createShop(mockShopDto)
        }

        // then
        coVerify(exactly = 1) { shopCommandService.createShop(mockShopDto) }
        assert(exception is ShopBranchInfoInvalidException)

        println("Test passed!!")
    }

    @Test
    @DisplayName("[create shop] 3. isBranch가 true인데 branchName이 null인 경우 실패 테스트")
    fun failCreateShopTest3(): Unit = runBlocking {
        // given
        val mockShopDto = generateDto().apply {
            isBranch = true
            branchName = null
        }

        // when
        val exception = shouldThrow<ShopBranchInfoInvalidException> {
            shopCommandService.createShop(mockShopDto)
        }

        // then
        coVerify(exactly = 1) { shopCommandService.createShop(mockShopDto) }
        assert(exception is ShopBranchInfoInvalidException)

        println("Test passed!!")
    }

    // create test용 dto를 생성해내는 메소드
    private fun generateDto(): ShopCommand.ShopCreateDto = ShopCommand.ShopCreateDto(
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