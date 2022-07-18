package team.bakkas.applicationquery.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import org.springframework.core.CoroutinesUtils
import org.springframework.stereotype.Service
import team.bakkas.common.exceptions.ShopNotFoundException
import team.bakkas.domaindynamo.entity.Shop
import team.bakkas.domainqueryservice.repository.ShopRepository

/** Shop에 대한 비지니스 로직을 구현하는 service layer class
 * @param shopRepository shop에 대한 cache hit이 구현된 repository
 */
@Service
class ShopService(
    private val shopRepository: ShopRepository
) {

    /** shopId와 shopName을 이용해서 shop을 가져오는 service method
     * @param shopId
     * @param shopName
     * @return @NotNull shop
     * @throws ShopNotFoundException
     */
    suspend fun findShopByIdAndName(shopId: String, shopName: String): Shop = withContext(Dispatchers.IO) {
        val shopMono = shopRepository.findShopByIdAndNameWithCaching(shopId, shopName)
        return@withContext CoroutinesUtils.monoToDeferred(shopMono).await()
            ?: throw ShopNotFoundException("Shop is not found!!")
    }

    /** 모든 shop의 리스트를 가져오는 메소드
     * @throws ShopNotFoundException
     * @return list of shop
     */
    suspend fun getAllShopList(): List<Shop> = withContext(Dispatchers.IO) {
        val shopFlow = shopRepository.getAllShopsWithCaching()
        val firstItem = CoroutinesUtils.monoToDeferred(shopFlow.first()).await()

        // cache hit에 실패하거나 repository 단계에서 예외가 발생하면 empty mono가 반환되기 때문에 first item은 null일수 있는 상황이 존재함
        checkNotNull(firstItem) {
            throw ShopNotFoundException("Shop is not found!!")
        }

        val shopList = mutableListOf<Shop>()

        shopFlow.buffer()
            .collect {
                val shop = CoroutinesUtils.monoToDeferred(it).await()
                shopList.add(shop!!)
            }

        check(shopList.size != 0) {
            throw ShopNotFoundException("Shop is not found!!")
        }

        shopList
    }
}