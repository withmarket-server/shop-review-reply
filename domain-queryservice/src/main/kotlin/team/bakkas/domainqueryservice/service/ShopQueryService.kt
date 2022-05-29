package team.bakkas.domainqueryservice.service

import org.springframework.stereotype.Service
import team.bakkas.domaindynamo.repository.ShopRepository

/** CQRS 패턴 중 Query 만을 담당하는 서비스 클래스. 모듈 이름과 클래스를 병렬적으로 놓으면 bean scope가 늘어난다.
 * @param shopRepository shop에 대한 repository
 */
@Service
class ShopQueryService(private val shopRepository: ShopRepository) {

    // shop의 id와 name을 통해서 shop을 하나 가져오는 메소드
    fun getShopByIdAndName(shopId: String, shopName: String) {
        val foundShop = shopRepository.findShopByIdAndName(shopId, shopName)


    }
}