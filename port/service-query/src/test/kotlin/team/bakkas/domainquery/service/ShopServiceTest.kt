package team.bakkas.domainquery.service

import io.kotest.assertions.throwables.shouldNotThrow
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.*
import kotlinx.coroutines.reactor.mono
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.core.CoroutinesUtils
import reactor.core.publisher.Mono
import team.bakkas.common.exceptions.shop.ShopNotFoundException
import team.bakkas.dynamo.shop.Shop
import team.bakkas.domainquery.repository.ifs.ShopReader
import team.bakkas.dynamo.shop.vo.*
import team.bakkas.dynamo.shop.vo.category.Category
import team.bakkas.dynamo.shop.vo.category.DetailCategory
import java.time.LocalTime

/**
 * ShopService에 대한 Mock test를 수행하는 클래스
 * @see <a href="https://notwoods.github.io/mockk-guidebook/docs/mocking/annotation/">Mock 사용법 참고</a>
 */
@ExtendWith(MockKExtension::class)
internal class ShopServiceTest {
    @MockK(relaxed = true)
    private lateinit var shopRepository: ShopReader // mock stub 오류를 안 잡아내게 설정

    private lateinit var shopService: ShopQueryServiceImpl

    @BeforeEach
    fun setUp() {
        shopService = spyk(ShopQueryServiceImpl(shopRepository)) // 실제 내부 로직을 테스트하기 위해 spyK로 선언한다
    }

    // 1-1, shop이 존재하지 않는 경우 테스트 (shop에 대한 key값이 잘못되었음)
    @Test
    @DisplayName("shop에 대한 key 정보가 잘못되어서 shop을 못 찾아오는 경우 테스트")
    fun failFindShop() = runBlocking {
        // given
        val shopId = "test-fake-key"
        val shopName = "fake shop"

        every { shopRepository.findShopByIdAndName(shopId, shopName) } returns mono {
            null
        }

        // when
        val shopMono = shopRepository.findShopByIdAndName(shopId, shopName) // mono 가져오기
        val shop = withContext(Dispatchers.IO) {
            CoroutinesUtils.monoToDeferred(shopMono).await()
        }

        // then
        verify(exactly = 1) {
            shopRepository.findShopByIdAndName(
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

        every { shopRepository.findShopByIdAndName(shopId, shopName) } returns mono {
            getMockShop(shopId, shopName, true)
        }

        // when
        val shopMono = shopRepository.findShopByIdAndName(shopId, shopName)
        val shop: Shop? = CoroutinesUtils.monoToDeferred(shopMono).await()

        // then
        verify(exactly = 1) { shopRepository.findShopByIdAndName(shopId, shopName) }
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

        // when
        // 잘못된 key 값을 주는 경우 empty mono를 반환하게 설정
        every { shopRepository.findShopByIdAndName(shopId, shopName) } returns Mono.empty()
        val result = shopService.findShopByIdAndName(shopId, shopName)

        // then
        verify(exactly = 1) { shopRepository.findShopByIdAndName(shopId, shopName) }
        coVerify(exactly = 1) { shopService.findShopByIdAndName(shopId, shopName) } // 코루틴의 경우 coVerify로 검증한다
        assertNull(result)

        println("[[service] findShopByIdAndName 실패 테스트] passed!!")
    }

    @Test
    @DisplayName("[service] findShopByIdAndName 성공 테스트")
    fun successFindShop2() = runBlocking {
        // given
        val shopId = "success-id"
        val shopName = "success-name"

        every { shopRepository.findShopByIdAndName(shopId, shopName) } returns mono {
            getMockShop(shopId, shopName, true)
        }

        // when
        val shop = shopService.findShopByIdAndName(shopId, shopName)

        // then
        verify(exactly = 1) { shopRepository.findShopByIdAndName(shopId, shopName) }
        coVerify(exactly = 1) { shopService.findShopByIdAndName(shopId, shopName) }
        shouldNotThrow<ShopNotFoundException> { shopService.findShopByIdAndName(shopId, shopName) } // 예외가 안 터져야함!
        assertNotNull(shop)
        shop?.let {
            assertEquals(it.shopId, shopId)
            assertEquals(it.shopName, shopName)
        }

        println("[[service] findShopByIdAndName 성공 테스트] passed!!")
    }

    // mock shop을 생성해내는 메소드
    private fun getMockShop(shopId: String, shopName: String, isOpen: Boolean) = Shop(
        shopId = shopId,
        shopName = shopName,
        salesInfo = SalesInfo(isOpen = isOpen, openTime = LocalTime.now(), closeTime = LocalTime.now()),
        addressInfo = AddressInfo(
            lotNumberAddress = "경상북도 경산시 조영동 307-1",
            roadNameAddress = "경상북도 경산시 대학로 318",
            detailAddress = null
        ),
        latLon = LatLon(latitude = 35.838597, longitude = 128.756576),
        shopImageInfo = ShopImageInfo(mainImage = "https://withmarket-image-bucket.s3.ap-northeast-2.amazonaws.com/c247bc62-e17f-43c1-90e9-60d566faaa3e.jpeg",
            representativeImageList = listOf("https://withmarket-image-bucket.s3.ap-northeast-2.amazonaws.com/c2570a85-1da7-4fec-9754-52a178e2abf5.jpeg")),
        branchInfo = BranchInfo(isBranch = false, branchName = null),
        categoryInfo = CategoryInfo(shopCategory = Category.ETC, shopDetailCategory = DetailCategory.ETC_ALL),
        totalScore = 0.0,
        reviewNumber = 0,
        shopDescription = "포오오스 마트!"
    )
}