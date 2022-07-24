package team.bakkas.domainshopcommand.service

import io.kotest.common.runBlocking
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.spyk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import team.bakkas.clientcommand.dto.shop.ShopCreateDto
import team.bakkas.common.category.Category
import team.bakkas.common.category.DetailCategory
import team.bakkas.domaindynamo.entity.Shop
import team.bakkas.domaindynamo.repository.ShopDynamoRepository
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@ExtendWith(MockKExtension::class)
internal class ShopCommandServiceTest {

    @MockK(relaxed = true)
    private lateinit var shopDynamoRepository: ShopDynamoRepository

    private lateinit var shopCommandService: ShopCommandService

    @BeforeEach
    fun setUp() {
        shopCommandService = spyk(ShopCommandService(shopDynamoRepository)) // shopCommandService를 spyK mock으로 선언
    }

    // 좌표가 안 맞아서 에러가 터지는 경우 테스트
    @Test
    @DisplayName("[create shop] 1. 위도 경도가 우리나라가 아니라서 실패하는 케이스")
    fun failCreateShopTest1(): Unit = runBlocking {
        // given
        val fakeLatitude = 90.0

        // when

        // then
    }

    // create test용 dto를 생성해내는 메소드
    private fun generateDto(): ShopCreateDto = ShopCreateDto(
        shopName = "카페 경사다",
        openTime = LocalTime.of(9, 0),
        closeTime = LocalTime.of(18, 0),
        lotNumberAddress = "경산시 가짜동",
        roadNameAddress = "경산시 대학로",
        latitude = 35.838597,
        longitude = 128.756576,
        isBranch = false,
        shopDescription = "테스트용 가게입니다",
        shopCategory = Category.FOOD_BEVERAGE,
        shopDetailCategory = DetailCategory.CAFE_BREAD
    )

    // dto로부터 shop을 생성해주는 메소드
    private fun generateShopFromDto(
        shopDto: ShopCreateDto,
        mainImageUrl: String,
        representativeImageUrlList: List<String>
    ) = Shop(
        shopId = UUID.randomUUID().toString(),
        shopName = shopDto.shopName,
        openTime = shopDto.openTime,
        closeTime = shopDto.closeTime,
        lotNumberAddress = shopDto.lotNumberAddress,
        roadNameAddress = shopDto.roadNameAddress,
        latitude = shopDto.latitude,
        longitude = shopDto.longitude,
        shopDescription = shopDto.shopDescription,
        isBranch = shopDto.isBranch,
        branchName = shopDto.branchName,
        shopCategory = shopDto.shopCategory,
        shopDetailCategory = shopDto.shopDetailCategory,
        mainImage = mainImageUrl,
        representativeImageList = representativeImageUrlList,
        createdAt = LocalDateTime.now(),
        averageScore = 0.0,
        isOpen = false,
        reviewNumber = 0,
        updatedAt = null
    )
}