package team.bakkas.domaindynamo.repository

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import team.bakkas.common.category.Category
import team.bakkas.common.category.DetailCategory
import team.bakkas.domaindynamo.entity.Shop
import java.time.LocalDateTime
import java.util.*

@SpringBootTest
internal class ShopRepositoryTest @Autowired constructor(
    val shopRepository: ShopRepository
) {

    @ParameterizedTest
    @CsvSource(value = ["ec1231-test001:역전할머니맥주 영남대점:false"], delimiter = ':')
    @DisplayName("Shop 하나를 생성한다")
    @Rollback(value = false)
    fun createShop(shopId: String, shopName: String, isOpen: Boolean) {
        val mockShop = getMockShop(UUID.randomUUID().toString(), shopName, isOpen)

        val shop = shopRepository.createShop(mockShop)

        println(shop)
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
        latitude = 35.837898,
        longitude = 128.754042,
        lotNumberAddress = "경산시 조영동 조영동 280-15",
        roadNameAddress = "경상북도 경산시 북부동 대학로59길 6",
        reviewNumber = 0,
        updatedAt = null,
        mainImage = "https://withmarket-image-bucket.s3.ap-northeast-2.amazonaws.com/ed0755dd-6afb-4ed5-8e7c-19658fd2e05c.jpeg",
        representativeImageList = listOf("https://withmarket-image-bucket.s3.ap-northeast-2.amazonaws.com/e7712904-03ab-4995-84cc-1e9e5fac16aa.jpeg"),
        shopDescription = "살얼음 맥주가 존맛탱인 도여비의 베스트 술집. 특히 짜파구리가 맛있어요!",
        shopCategory = Category.FOOD_BEVERAGE,
        shopDetailCategory = DetailCategory.FOOD_EXTRA
    )
}