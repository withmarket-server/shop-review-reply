package team.bakkas.domainqueryservice.service

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import team.bakkas.clientmobilequery.dto.ShopSimpleReadDto
import team.bakkas.common.ResultFactory
import team.bakkas.common.Results
import team.bakkas.common.exceptions.RequestParamLostException
import team.bakkas.common.exceptions.ShopNotFoundException
import team.bakkas.domaindynamo.entity.Shop
import team.bakkas.domaindynamo.repository.ShopRepository

/** CQRS 패턴 중 Query 만을 담당하는 서비스 클래스. 모듈 이름과 클래스를 병렬적으로 놓으면 bean scope가 늘어난다.
 * @param shopRepository shop에 대한 repository
 */
@Service
class ShopQueryService(private val shopRepository: ShopRepository) {

    // shop의 id와 name을 통해서 shop을 하나 가져오는 메소드
    fun getShopByIdAndName(
        shopId: String?,
        shopName: String?
    ): ResponseEntity<Results.SingleResult<ShopSimpleReadDto>> {

        // 예외 처리 -> key 조건이 null로 제시된 경우
        if (shopId == null || shopName == null || shopId.isEmpty() || shopName.isEmpty())
            throw RequestParamLostException("잘못된 형식의 검색. shopId 또는 shopName을 확인하십시오.")

        // shop이 존재하지 않는 경우 -> shopNotFoundException을 뱉어준다
        val foundShop = shopRepository.findShopByIdAndName(shopId, shopName)
            ?: throw ShopNotFoundException("There's no shop!!")

        // null 검사를 한 상태이기 때문에 null-safe한 상황이다.
        val responseShop = toSimpleReadDto(foundShop)

        return ResponseEntity.ok(ResultFactory.getSingleResult(responseShop))
    }

    // 존재하는 모든 shop을 반환하는 메소드. SimpleReadDto로 포장하여 리턴한다
    fun getAllSimpleShops(): ResponseEntity<Results.MultipleResult<ShopSimpleReadDto>> {
        val shopList = shopRepository.findAllShop()

        // shop이 아무것도 검색되지 않는 경우는 exception을 뱉어준다
        if (shopList.isEmpty())
            throw ShopNotFoundException("There's no shop!!")

        val responseShopList = shopList.map { shop ->
            toSimpleReadDto(shop)
        }.toList()

        return ResponseEntity.ok(ResultFactory.getMultipleResult(responseShopList))
    }

    // entity -> simpleDto
    private fun toSimpleReadDto(foundShop: Shop) = ShopSimpleReadDto(
        shopId = foundShop.shopId,
        shopName = foundShop.shopName,
        isOpen = foundShop.isOpen,
        lotNumberAddress = foundShop.lotNumberAddress,
        roadNameAddress = foundShop.roadNameAddress,
        latitude = foundShop.latitude,
        longitude = foundShop.longitude,
        averageScore = foundShop.averageScore,
        reviewNumber = foundShop.reviewNumber,
        mainImage = foundShop.mainImage,
        shopDescription = foundShop.shopDescription,
        shopCategory = foundShop.shopCategory,
        shopDetailCategory = foundShop.shopDetailCategory
    )
}