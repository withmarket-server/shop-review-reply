package team.bakkas.domainshopcommand.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.core.CoroutinesUtils
import org.springframework.stereotype.Service
import team.bakkas.clientcommand.dto.shop.ShopCreateDto
import team.bakkas.common.exceptions.RegionNotKoreaException
import team.bakkas.common.exceptions.ShopBranchInfoInvalidException
import team.bakkas.domaindynamo.entity.Shop
import team.bakkas.domaindynamo.repository.ShopDynamoRepository
import java.time.LocalDateTime
import java.util.UUID

/** shop의 command query를 담당하는 비지니스 로직을 정의하는 service 클래스
 * @param shopDynamoRepository dynamoDB에 접근하는데 사용하는 Data access layer의 repository
 */
@Service
class ShopCommandService(
    private val shopDynamoRepository: ShopDynamoRepository
) {

    /** shop을 생성하는 비지니스 로직을 정의하는 메소드
     * @param shopCreateDto shop을 create 하는데 사용하는 dto parameter
     */
    suspend fun createShop(shopCreateDto: ShopCreateDto): Shop = withContext(Dispatchers.IO) {

        // TODO 코드의 응집성을 위해서 validator로 뽑아내기
        with(shopCreateDto) {
            // 1. latitude, longitude가 우리나라 지역이 아닌 경우에 대한 예외 처리
            check(isInSouthKorea(latitude, longitude)) {
                throw RegionNotKoreaException("주어진 좌표가 한국(South Korea)내에 존재하지 않습니다.")
            }

            // 2. 지점 정보가 잘못되었는지 검증
            check((isBranch == true && !branchName.isNullOrEmpty()) || (isBranch == false && branchName.isNullOrEmpty())) {
                throw ShopBranchInfoInvalidException("본점/지점 정보가 잘못 주어졌습니다.")
            }
        }

        val generatedShop = generateShopFromDto(shopCreateDto)
        val shopMono = shopDynamoRepository.createShopAsync(generatedShop)
        val shopDeferred = CoroutinesUtils.monoToDeferred(shopMono)

        shopDeferred.await()

        generatedShop
    }

    // TODO shop을 수정하는 메소드

    // TODO shop을 삭제하는 메소드

    // 우리나라 지역이 맞는지 체크를 수행해주는 수행해주는 메소드
    private fun isInSouthKorea(latitude: Double, longitude: Double): Boolean {
        val latitudeSatisfied = latitude > 125.06666667 && latitude < 131.87222222
        val longitudeSatisfied = longitude > 33.10000000 && longitude < 38.45000000

        return latitudeSatisfied && longitudeSatisfied
    }

    private fun generateShopFromDto(
        shopDto: ShopCreateDto
    ) = Shop(
        shopId = UUID.randomUUID().toString(),
        shopName = shopDto.shopName,
        openTime = shopDto.openTime,
        closeTime = shopDto.closeTime,
        lotNumberAddress = shopDto.lotNumberAddress,
        roadNameAddress = shopDto.roadNameAddress,
        latitude = shopDto.latitude,
        longitude = shopDto.longitude,
        shopDescription = shopDto.shopDescription,
        isBranch = shopDto.isBranch,
        branchName = shopDto.branchName,
        shopCategory = shopDto.shopCategory,
        shopDetailCategory = shopDto.shopDetailCategory,
        mainImage = shopDto.mainImageUrl,
        representativeImageList = shopDto.representativeImageUrlList,
        createdAt = LocalDateTime.now(),
        averageScore = 0.0,
        isOpen = false,
        reviewNumber = 0,
        updatedAt = null
    )
}