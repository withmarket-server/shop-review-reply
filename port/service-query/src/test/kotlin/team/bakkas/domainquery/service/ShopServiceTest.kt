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
import team.bakkas.domainquery.reader.ifs.ShopReader
import team.bakkas.dynamo.shop.vo.*
import team.bakkas.dynamo.shop.vo.category.Category
import team.bakkas.dynamo.shop.vo.category.DetailCategory
import team.bakkas.dynamo.shop.vo.sale.Status
import java.time.LocalTime

/**
 * ShopService에 대한 Mock test를 수행하는 클래스
 * @see <a href="https://notwoods.github.io/mockk-guidebook/docs/mocking/annotation/">Mock 사용법 참고</a>
 */
@ExtendWith(MockKExtension::class)
internal class ShopServiceTest {
    @MockK(relaxed = true)
    private lateinit var shopReader: ShopReader // mock stub 오류를 안 잡아내게 설정

    private lateinit var shopService: ShopQueryServiceImpl

    @BeforeEach
    fun setUp() {
        shopService = spyk(ShopQueryServiceImpl(shopReader)) // 실제 내부 로직을 테스트하기 위해 spyK로 선언한다
    }

    // 1-1, shop이 존재하지 않는 경우 테스트 (shop에 대한 key값이 잘못되었음)
    @Test
    @DisplayName("shop에 대한 key 정보가 잘못되어서 shop을 못 찾아오는 경우 테스트입니다")
    fun failFindShop() = runBlocking {
        // given
        val shopId = "test-fake-key"

        every { shopReader.findShopById(shopId) } returns Mono.empty()

        // when
        val shopMono = shopReader.findShopById(shopId) // mono 가져오기
        val shop = withContext(Dispatchers.IO) {
            CoroutinesUtils.monoToDeferred(shopMono).await()
        }

        // then
        verify(exactly = 1) { shopReader.findShopById(shopId) } // repository 메소드가 불렸는지 검증
        assertNull(shop)
    }

    // 1-2. shop이 존재하는 경우 테스트
    @Test
    @DisplayName("shop을 가져올 수 있는 케이스")
    fun successFindShop(): Unit = runBlocking {
        // given
        val shopId = "correct-shop-id"
        val shopName = "correct-shop-name"

        every { shopReader.findShopById(shopId) } returns Mono.just(
            getMockShop(
                shopId,
                shopName,
                Status.OPEN
            )
        )

        // when
        val shopMono = shopReader.findShopById(shopId)
        val shop: Shop? = CoroutinesUtils.monoToDeferred(shopMono).await()

        // then
        verify(exactly = 1) { shopReader.findShopById(shopId) }
        assertNotNull(shop)
        shop?.let {
            assertEquals(it.shopId, shopId)
            assertEquals(it.shopName, shopName)
        }
    }

    // 1-3. service logic 검증 -> Key가 잘못된 경우
    @Test
    @DisplayName("[service] findShopByIdAndName 실패 테스트")
    fun failFindShop2() = runBlocking {
        // given
        val shopId = "fake-id"

        // when
        // 잘못된 key 값을 주는 경우 empty mono를 반환하게 설정
        every { shopReader.findShopById(shopId) } returns Mono.empty()
        val result = shopService.findShopById(shopId)

        // then
        verify(exactly = 1) { shopReader.findShopById(shopId) }
        coVerify(exactly = 1) { shopService.findShopById(shopId) } // 코루틴의 경우 coVerify로 검증한다
        assertNull(result)
    }

    @Test
    @DisplayName("[service] findShopByIdAndName 성공 테스트")
    fun successFindShop2(): Unit = runBlocking {
        // given
        val shopId = "success-id"
        val shopName = "success-name"

        every { shopReader.findShopById(shopId) } returns mono {
            getMockShop(shopId, shopName, Status.OPEN)
        }

        // when
        val shop = shopService.findShopById(shopId)

        // then
        verify(exactly = 1) { shopReader.findShopById(shopId) }
        coVerify(exactly = 1) { shopService.findShopById(shopId) }
        shouldNotThrow<ShopNotFoundException> { shopService.findShopById(shopId) } // 예외가 안 터져야함!
        assertNotNull(shop)
        shop?.let {
            assertEquals(it.shopId, shopId)
            assertEquals(it.shopName, shopName)
        }
    }

    // mock shop을 생성해내는 메소드
    private fun getMockShop(shopId: String, shopName: String, status: Status) = Shop(
        shopId = shopId,
        shopName = shopName,
        salesInfo = SalesInfo(status = status, openTime = LocalTime.now(), closeTime = LocalTime.now()),
        addressInfo = AddressInfo(
            lotNumberAddress = "경상북도 경산시 조영동 307-1",
            roadNameAddress = "경상북도 경산시 대학로 318",
            detailAddress = null
        ),
        latLon = LatLon(latitude = 35.838597, longitude = 128.756576),
        shopImageInfo = ShopImageInfo(
            mainImage = "https://withmarket-image-bucket.s3.ap-northeast-2.amazonaws.com/c247bc62-e17f-43c1-90e9-60d566faaa3e.jpeg",
            representativeImageList = listOf("https://withmarket-image-bucket.s3.ap-northeast-2.amazonaws.com/c2570a85-1da7-4fec-9754-52a178e2abf5.jpeg")
        ),
        branchInfo = BranchInfo(isBranch = false, branchName = null),
        categoryInfo = CategoryInfo(shopCategory = Category.ETC, shopDetailCategory = DetailCategory.ETC_ALL),
        totalScore = 0.0,
        reviewNumber = 0,
        shopDescription = "포오오스 마트!",
        memberId = "123123-33123-33123"
    )
}