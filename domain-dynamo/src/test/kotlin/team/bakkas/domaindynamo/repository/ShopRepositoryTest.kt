package team.bakkas.domaindynamo.repository

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.util.StopWatch
import team.bakkas.common.category.Category
import team.bakkas.common.category.DetailCategory
import team.bakkas.domaindynamo.entity.Shop
import java.time.LocalDateTime
import java.util.*

@SpringBootTest
internal class ShopRepositoryTest @Autowired constructor(
    private val shopRepository: ShopRepository
) {

    @ParameterizedTest
    @CsvSource(value = ["ec1231-test001:카페봄봄 영남대점:false"], delimiter = ':')
    @DisplayName("Shop 하나를 생성한다")
    @Rollback(value = false)
    fun createShop(shopId: String, shopName: String, isOpen: Boolean) {
        val mockShop = getMockShop(UUID.randomUUID().toString(), shopName, isOpen)

        val shop = shopRepository.createShop(mockShop)

        println(shop)
    }

    @ParameterizedTest
    @CsvSource(value = ["33daf043-7f36-4a52-b791-018f9d5eb218:역전할머니맥주 영남대점"], delimiter = ':')
    @DisplayName("Shop 하나를 찾아온다")
    fun findOneShop(shopId: String, shopName: String) {
        val foundShop = shopRepository.findShopByIdAndName(shopId, shopName)

        assertNotNull(foundShop)
        with(foundShop!!) {
            assertEquals(this.shopId, shopId)
            assertEquals(this.shopName, shopName)
        }

        println(foundShop)
        println("Test passed!!")
    }

    @ParameterizedTest
    @CsvSource(value = ["33daf043-7f36-4a52-b791-018f9d5eb218:역전할머니맥주 영남대점"], delimiter = ':')
    @DisplayName("dynamo에서 가져오는 것과 redis에서 가져오는 것의 속도 비교")
    fun compareFindOneShop(shopId: String, shopName: String) {
        val stopWatch = StopWatch()
        stopWatch.start()

        var foundShop = shopRepository.findShopByIdAndName(shopId, shopName)

        stopWatch.stop()

        println("shop을 가져오는데 걸린 시간: ${stopWatch.totalTimeSeconds}")

        assertNotNull(foundShop)
        with(foundShop!!) {
            assertEquals(this.shopId, shopId)
            assertEquals(this.shopName, shopName)
        }

        println("Test passed!!")
    }

    @Test
    @DisplayName("shop 테이블에 존재하는 모든 데이터를 긁어온다")
    fun 모든shop을가져온다() {
        val stopWatch = StopWatch()
        stopWatch.start()

        val shopList = shopRepository.findAllShop()

        stopWatch.stop()

        println("shopList을 가져오는데 걸린 시간: ${stopWatch.totalTimeSeconds}")

        shopList.forEach { shop -> println(shop) }
    }

    @ParameterizedTest
    @CsvSource(value = ["6b0999de-0bf1-4378-bd32-4ac808c2ae45:Hash"], delimiter = ':')
    fun deleteOneShop(shopId: String, shopName: String) {
        shopRepository.deleteShop(shopId, shopName)

        println("Test passed!!")
    }

    fun getMockShop(shopId: String, shopName: String, isOpen: Boolean): Shop = Shop(
        shopId = shopId,
        shopName = shopName,
        isOpen = isOpen,
        openTime = LocalDateTime.now(),
        closeTime = LocalDateTime.now(),
        createdAt = LocalDateTime.now(),
        averageScore = 0.0,
        latitude = 35.837129,
        longitude = 128.753736,
        lotNumberAddress = "경상북도 경산시 대동 168-1",
        roadNameAddress = "경상북도 경산시 대학로 291",
        reviewNumber = 0,
        updatedAt = null,
        mainImage = "https://withmarket-image-bucket.s3.ap-northeast-2.amazonaws.com/5b175751-6070-4c86-9905-84b7623df977.jpeg",
        representativeImageList = listOf("https://withmarket-image-bucket.s3.ap-northeast-2.amazonaws.com/54ea83a0-f308-4ee0-adef-5a10b2b7d7dd.jpeg"),
        isBranch = false,
        branchName = null,
        shopDescription = "영남대의 자랑! 카페봄봄 영남대 본점입니다!",
        shopCategory = Category.FOOD_BEVERAGE,
        shopDetailCategory = DetailCategory.BEVERAGE_COFFEE_MILK_PRODUCT
    )
}