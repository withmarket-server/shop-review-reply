package team.bakkas.domainqueryservice.service

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import team.bakkas.domaindynamo.repository.ShopDynamoRepository

@SpringBootTest
internal class ShopQueryServiceTest @Autowired constructor(
    val shopRepository: ShopDynamoRepository
) {

    @ParameterizedTest
    @CsvSource(value = ["c8b05f28-1acc-42c2-a027-dc2bceea7356:역전할머니맥주 영남대점"], delimiter = ':')
    @DisplayName("shop 하나를 가져온다")
    fun `shop 하나를 가져온다`(shopId: String, shopName: String) {
        val foundShop = shopRepository.findShopByIdAndName(shopId, shopName)

        println(foundShop)
    }

    @ParameterizedTest
    @CsvSource(value = ["10323299-b10b-409c-88d2-66e20e4dabe6:잘못된할맥"], delimiter = ':')
    @DisplayName("shop 하나를 가져온다")
    fun `shopName이 잘못되어서 가져오지 못한다`(shopId: String, shopName: String) {
        val foundShop = shopRepository.findShopByIdAndName(shopId, shopName)

        with(foundShop) {
            Assertions.assertNull(this)
        }
    }

    @Test
    @DisplayName("모든 shop을 가져온다")
    fun `모든 shop을 가져온다`() {
        val shopList = shopRepository.findAllShop()

        println(shopList)
    }
}