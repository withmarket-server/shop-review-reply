package team.bakkas.dao.repository.dynamo

import kotlinx.coroutines.*
import kotlinx.coroutines.reactor.awaitSingle
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import software.amazon.awssdk.enhanced.dynamodb.Key
import team.bakkas.dynamo.shop.Shop
import team.bakkas.dynamo.shop.vo.*
import team.bakkas.dynamo.shop.vo.category.Category
import team.bakkas.dynamo.shop.vo.category.DetailCategory
import team.bakkas.dynamo.shop.vo.sale.Days
import team.bakkas.dynamo.shop.vo.sale.Status
import java.time.LocalTime
import java.util.*

@SpringBootTest
internal class ShopDynamoRepositoryTest @Autowired constructor(
    private val shopDynamoRepository: ShopDynamoRepositoryImpl
) {
    @ParameterizedTest
    @CsvSource(value = ["브라이언 인쇄소:false"], delimiter = ':')
    @DisplayName("Shop 하나를 생성한다")
    @Rollback(value = false)
    fun createShopAsync(shopName: String, isOpen: Boolean): Unit = runBlocking {
        val mockShop = getMockShop(UUID.randomUUID().toString(), shopName, isOpen)

        val createdShopMono = shopDynamoRepository.createShop(mockShop).awaitSingle()

        println(createdShopMono.toString())
    }

    fun generateKey(shopId: String, shopName: String) = Key.builder()
        .partitionValue(shopId)
        .sortValue(shopName)
        .build()

    private fun getMockShop(shopId: String, shopName: String, isOpen: Boolean) = Shop(
        shopId = shopId,
        shopName = shopName,
        salesInfo = SalesInfo(status = Status.OPEN, openTime = LocalTime.now(), closeTime = LocalTime.now(), restDayList = listOf(Days.SUN)),
        addressInfo = AddressInfo(
            lotNumberAddress = "경상북도 경산시 조영동 307-1",
            roadNameAddress = "경상북도 경산시 대학로 318",
            detailAddress = "인쇄소 1층"
        ),
        latLon = LatLon(latitude = 35.838597, longitude = 128.756576),
        shopImageInfo = ShopImageInfo(mainImage = "https://withmarket-image-bucket.s3.ap-northeast-2.amazonaws.com/c247bc62-e17f-43c1-90e9-60d566faaa3e.jpeg",
            representativeImageList = listOf("https://withmarket-image-bucket.s3.ap-northeast-2.amazonaws.com/c2570a85-1da7-4fec-9754-52a178e2abf5.jpeg")),
        branchInfo = BranchInfo(isBranch = false, branchName = null),
        categoryInfo = CategoryInfo(shopCategory = Category.MART, shopDetailCategory = DetailCategory.SUPER_MARKET),
        deliveryTipPerDistanceList = listOf(DeliveryTipPerDistance(3.0, 2000)),
        totalScore = 0.0,
        reviewNumber = 0,
        shopDescription = "포오오스 마트!"
    )
}