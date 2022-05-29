package team.bakkas.domaindynamo.repository

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import team.bakkas.domaindynamo.entity.Shop
import java.time.LocalDateTime
import java.util.*

@SpringBootTest
internal class ShopRepositoryTest @Autowired constructor(
    val shopRepository: ShopRepository
){
    @ParameterizedTest
    @CsvSource(value = ["ec1231-test001:태스트할맥:false"], delimiter = ':')
    @DisplayName("Shop 하나를 생성한다")
    @Rollback(value = false)
    fun createShop(shopId: String, shopName: String, isOpen: Boolean) {
        val mockShop = getMockShop(UUID.randomUUID().toString(), shopName, isOpen)

        val shop = shopRepository.createShop(mockShop)

        println(shop)
    }

    @ParameterizedTest
    @CsvSource(value = ["ec21, 테스트맛도리집"])
    @DisplayName("Shop 하나를 찾아오는데 성공한다")
    fun findShopByIdAndName(shopId: String, shopName: String): Unit {
        val foundShop = shopRepository.findShopByIdAndName(shopId, shopName)

        with(foundShop) {
            Assertions.assertNotNull(this)
            Assertions.assertEquals(this!!.shopId, shopId)
            Assertions.assertEquals(this!!.shopName, shopName)
            println("Test passed")
        }
    }

    @ParameterizedTest
    @CsvSource(value = ["ec21, 테스트맛집"])
    @DisplayName("SortKey를 잘못줘서 null을 받아버린다")
    fun shop을못찾아온다1(shopId: String, shopName: String): Unit {
        val foundShop = shopRepository.findShopByIdAndName(shopId, shopName)

        with(foundShop) {
            Assertions.assertNull(this)
            println("Test Passed!")
        }
    }

    @ParameterizedTest
    @CsvSource(value = ["ec21, 테스트맛도리집"])
    @DisplayName("Shop 하나를 삭제시키는데 성공한다")
    fun shop을삭제하는데실패한다(shopId: String, shopName: String) {
        // when
        shopRepository.deleteShop(shopId, shopName)

        // then
        val foundShop = shopRepository.findShopByIdAndName(shopId, shopName)

        with(foundShop) {
            Assertions.assertNull(this)
            println("Test Passed!")
        }
    }

    @Test
    @DisplayName("shop 테이블에 존재하는 모든 데이터를 긁어온다")
    fun 모든shop을가져온다() {
        val shopList = shopRepository.findAllShop()

        shopList.forEach { shop -> println(shop) }
    }

    fun getMockShop(shopId: String, shopName: String, isOpen: Boolean): Shop = Shop(
        shopId = shopId,
        shopName = shopName,
        isOpen = isOpen,
        openTime = LocalDateTime.now(),
        closeTime = LocalDateTime.now(),
        createdAt = LocalDateTime.now(),
        averageScore = 0.0,
        latitude = 10.0,
        longitude = 10.0,
        lotNumberAddress = "경산시 조영동 307-1",
        roadNameAddress = "경산시 대학로",
        reviewNumber = 0,
        updatedAt = null,
        mainImage = "https://withmarket-image-bucket.s3.ap-northeast-2.amazonaws.com/ed0755dd-6afb-4ed5-8e7c-19658fd2e05c.jpeg",
        representativeImageList = listOf("https://withmarket-image-bucket.s3.ap-northeast-2.amazonaws.com/e7712904-03ab-4995-84cc-1e9e5fac16aa.jpeg")
    )

}