package team.bakkas.domainqueryservice.service

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.reactor.mono
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.core.CoroutinesUtils
import reactor.core.publisher.Mono
import team.bakkas.common.category.Category
import team.bakkas.common.category.DetailCategory
import team.bakkas.common.exceptions.ShopNotFoundException
import team.bakkas.domaindynamo.entity.Shop
import team.bakkas.domainqueryservice.repository.ShopRepository
import team.bakkas.domainqueryservice.service.ShopService
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * ShopService에 대한 Mock test를 수행하는 클래스
 * @see <a href="https://notwoods.github.io/mockk-guidebook/docs/mocking/annotation/">Mock 사용법 참고</a>
 */
@ExtendWith(MockKExtension::class)
internal class ShopServiceTest {
    @MockK(relaxed = true)
    private lateinit var shopRepository: ShopRepository // mock stub 오류를 안 잡아내게 설정

    private lateinit var shopService: ShopService

    @BeforeEach
    fun setUp() {
        shopService = spyk(ShopService(shopRepository)) // 실제 내부 로직을 테스트하기 위해 spyK로 선언한다
    }

    // 1-1, shop이 존재하지 않는 경우 테스트 (shop에 대한 key값이 잘못되었음)
    @Test
    @DisplayName("shop에 대한 key 정보가 잘못되어서 shop을 못 찾아오는 경우 테스트")
    fun failFindShop() = runBlocking {
        // given
        val shopId = "test-fake-key"
        val shopName = "fake shop"

        every { shopRepository.findShopByIdAndNameWithCaching(shopId, shopName) } returns mono {
            null
        }

        // when
        val shopMono = shopRepository.findShopByIdAndNameWithCaching(shopId, shopName) // mono 가져오기
        val shop = withContext(Dispatchers.IO) {
            CoroutinesUtils.monoToDeferred(shopMono).await()
        }

        // then
        verify(exactly = 1) {
            shopRepository.findShopByIdAndNameWithCaching(
                shopId,
                shopName
            )
        } // repository 메소드가 불렸는지 검증
        assertNull(shop)

        println("[shop에 대한 key 정보가 잘못되어서 shop을 못 찾아오는 경우 테스트] passed!!")
    }

    // 1-2. shop이 존재하는 경우 테스트
    @Test
    @DisplayName("shop을 가져올 수 있는 케이스")
    fun successFindShop() = runBlocking {
        // given
        val shopId = "correct-shop-id"
        val shopName = "correct-shop-name"

        every { shopRepository.findShopByIdAndNameWithCaching(shopId, shopName) } returns mono {
            getMockShop(shopId, shopName, true)
        }

        // when
        val shopMono = shopRepository.findShopByIdAndNameWithCaching(shopId, shopName)
        val shop: Shop? = CoroutinesUtils.monoToDeferred(shopMono).await()

        // then
        verify(exactly = 1) { shopRepository.findShopByIdAndNameWithCaching(shopId, shopName) }
        assertNotNull(shop)
        shop?.let {
            assertEquals(it.shopId, shopId)
            assertEquals(it.shopName, shopName)
        }

        println("[shop을 가져올 수 있는 케이스] passed!!")
    }

    // 1-3. service logic 검증 -> Key가 잘못된 경우
    @Test
    @DisplayName("[service] findShopByIdAndName 실패 테스트")
    fun failFindShop2() = runBlocking {
        // given
        val shopId = "fake-id"
        val shopName = "fake-name"

        // 잘못된 key 값을 주는 경우 empty mono를 반환하게 설정
        every { shopRepository.findShopByIdAndNameWithCaching(shopId, shopName) } returns mono {
            null
        }

        // when
        val exception = shouldThrow<ShopNotFoundException> { shopService.findShopByIdAndName(shopId, shopName) }

        // then
        verify(exactly = 1) { shopRepository.findShopByIdAndNameWithCaching(shopId, shopName) }
        coVerify(exactly = 1) { shopService.findShopByIdAndName(shopId, shopName) } // 코루틴의 경우 coVerify로 검증한다
        assert(exception is ShopNotFoundException) // 무조건 shopNotFoundException이 터져야한다

        println("[[service] findShopByIdAndName 실패 테스트] passed!!")
    }

    @Test
    @DisplayName("[service] findShopByIdAndName 성공 테스트")
    fun successFindShop2() = runBlocking {
        // given
        val shopId = "success-id"
        val shopName = "success-name"

        every { shopRepository.findShopByIdAndNameWithCaching(shopId, shopName) } returns mono {
            getMockShop(shopId, shopName, true)
        }

        // when
        val shop = shopService.findShopByIdAndName(shopId, shopName)

        // then
        verify(exactly = 1) { shopRepository.findShopByIdAndNameWithCaching(shopId, shopName) }
        coVerify(exactly = 1) { shopService.findShopByIdAndName(shopId, shopName) }
        shouldNotThrow<ShopNotFoundException> { shopService.findShopByIdAndName(shopId, shopName) } // 예외가 안 터져야함!
        assertNotNull(shop)
        shop.let {
            assertEquals(it.shopId, shopId)
            assertEquals(it.shopName, shopName)
        }

        println("[[service] findShopByIdAndName 성공 테스트] passed!!")
    }

    @Test
    @DisplayName("[service] shop에서 list를 가져오는데 실패하는 메소드")
    fun failGetShopList() = runBlocking {
        // given
        every { shopRepository.getAllShopsWithCaching() } returns flowOf( Mono.empty() ) // 비어있는 플로우를 반환시킨다

        // when
        val exception = shouldThrow<ShopNotFoundException> { shopService.getAllShopList() }

        // then
        verify(exactly = 1) { shopRepository.getAllShopsWithCaching() }
        coVerify(exactly = 1) { shopService.getAllShopList() }
        assert(exception is ShopNotFoundException)

        println("[[service] shop에서 list를 가져오는데 실패하는 메소드] passed!!")
    }

    // mock shop을 생성해내는 메소드
    private fun getMockShop(shopId: String, shopName: String, isOpen: Boolean) = Shop(
        shopId = shopId,
        shopName = shopName,
        isOpen = isOpen,
        openTime = LocalTime.now(),
        closeTime = LocalTime.now(),
        createdAt = LocalDateTime.now(),
        averageScore = 0.0,
        latitude = 35.838597,
        longitude = 128.756576,
        lotNumberAddress = "경상북도 경산시 조영동 307-1",
        roadNameAddress = "경상북도 경산시 대학로 318",
        reviewNumber = 0,
        updatedAt = null,
        mainImage = "https://withmarket-image-bucket.s3.ap-northeast-2.amazonaws.com/c247bc62-e17f-43c1-90e9-60d566faaa3e.jpeg",
        representativeImageList = listOf("https://withmarket-image-bucket.s3.ap-northeast-2.amazonaws.com/c2570a85-1da7-4fec-9754-52a178e2abf5.jpeg"),
        isBranch = false,
        branchName = null,
        shopDescription = "포오오스 마트!",
        shopCategory = Category.ETC,
        shopDetailCategory = DetailCategory.ETC_ALL
    )
}