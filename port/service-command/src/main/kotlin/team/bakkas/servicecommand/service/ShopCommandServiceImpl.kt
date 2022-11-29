package team.bakkas.servicecommand.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import team.bakkas.clientcommand.shop.ShopCommand
import team.bakkas.dynamo.shop.Shop
import team.bakkas.dynamo.shop.extensions.*
import team.bakkas.servicecommand.service.ifs.ShopCommandService
import team.bakkas.repository.ifs.dynamo.ShopDynamoRepository
import team.bakkas.repository.ifs.redis.ShopRedisRepository

/**
 * ShopCommandServiceImpl
 * ShopCommandService의 구현체. UseCase layer를 담당한다.
 * @param shopDynamoRepository dynamo repository class for Shop domain
 * @param shopRedisRepository redis repository class for Shop domain
 */
@Service
class ShopCommandServiceImpl(
    private val shopDynamoRepository: ShopDynamoRepository,
    private val shopRedisRepository: ShopRedisRepository
) : ShopCommandService {

    override fun createShop(shop: Shop): Mono<Shop> {
        return shopDynamoRepository.createShop(shop) // shop을 dynamo에 기록하고
            .doOnSuccess { shopRedisRepository.cacheShop(it).subscribe() } // 성공시 redis에 저장한다
    }

    override fun deleteShop(shopId: String): Mono<Shop> {
        return shopDynamoRepository.deleteShop(shopId) // shop을 dynamo에서 삭제시키고
            .doOnSuccess { shopRedisRepository.deleteShop(shopId).subscribe() } // 삭제에 성공하면 redis에 있는 shop도 삭제
    }

    /**
     * softDeleteShop(shopId: String)
     * soft delete 삭제 정책에 의해 Shop을 삭제하는 메소드
     * record system 상에서는 soft delete를 적용하고, cache db에는 완전 삭제를 구현한다
     * @param shopId 가게의 id
     */
    override fun softDeleteShop(shopId: String): Mono<Shop> {
        return shopDynamoRepository.softDeleteShop(shopId) // dynamoDB의 data를 soft delete 처리하고
            .doOnSuccess { shopRedisRepository.deleteShop(shopId).subscribe() } // Redis의 데이터는 날려버린다
    }

    /**
     * applyCreateReview(shopId: String, reviewScore: Double)
     * review가 생성됨에 따라서 shop의 totalScore, reviewNumber를 조정해준 뒤, 이를 record system, cache db에 반영해주는 메소드
     * @param shopId 가게의 id 정보
     * @param reviewScore 반영하려는 리뷰의 점수 정보
     * @return Mono<Shop> result of applying review creation
     */
    override fun applyCreateReview(shopId: String, reviewScore: Double): Mono<Shop> {
        return shopDynamoRepository.findShopById(shopId) // shopId, shopName을 이용해서 shop을 찾아내고
            .map { it.applyReviewCreate(reviewScore) } // shop에 reviewScore를 반영해서 변화시키고
            .flatMap { shopDynamoRepository.createShop(it) } // 다시 저장한다
            .doOnSuccess { shopRedisRepository.cacheShop(it).subscribe() } // 저장에 성공하면 동시에 redis에 캐싱을 수행한다
    }

    /**
     * applyDeleteReview(shopId: String, reviewScore: Double)
     * review가 삭제됨에 따라 shop의 totalScore, reviewNumber를 조정해준 뒤, 이를 record system, cache db에 반영해주는 메소드
     * @param shopId 가게의 id 정보
     * @param reviewScore 반영하려는 review의 평점 정보
     * @return Mono<Shop> result of applying review deletion
     */
    override fun applyDeleteReview(shopId: String, reviewScore: Double): Mono<Shop> {
        return shopDynamoRepository.findShopById(shopId) // shopId, shopName을 기반으로 shop을 찾아내고
            .map { it.applyReviewDelete(reviewScore) } // reviewScore를 반영하고
            .flatMap { shopDynamoRepository.createShop(it) } // dynamo에 밀어넣고
            .doOnSuccess { shopRedisRepository.cacheShop(it).subscribe() } // redis에 다시 캐싱한다
    }

    /**
     * updateShop(updateRequest: ShopCommand.UpdateRequest)
     * updateRequest를 받아서 shop의 정보 수정을 반영해주는 메소드
     * shopId를 제외한 모든 파라미터들은 모두 nullable이며, domain extensions의 메소드에 의해 가게 수정 정보들이 반영된다.
     * @param updateRequest update request dto class
     * @return Mono<Shop> result of the applying shop update
     */
    override fun updateShop(updateRequest: ShopCommand.UpdateRequest): Mono<Shop> {
        return shopDynamoRepository.findShopById(updateRequest.shopId)
            .map { it.changeShopName(updateRequest.shopName) } // 가게 이름 변경
            .map { it.changeMainImage(updateRequest.mainImage) } // 메인 이미지 변경
            .map { it.changeRepresentativeImageList(updateRequest.representativeImageUrlList) } // 대표 이미지 목록 변경
            .map { changeOpenTimeRange(it, updateRequest.openTimeRange) } // 가게 오픈시간, 종료시간 변경
            .map { it.changeRestDayList(updateRequest.restDayList) } // 휴무일 변경
            .flatMap { shopDynamoRepository.createShop(it) } // 변경이 적용된 가게를 dynamo에 저장하고
            .doOnSuccess { shopRedisRepository.cacheShop(it).subscribe() } // 그걸 그대로 redis에도 전파한다
    }

    /**
     * changeOpenTimeRange(shop: Shop, openTimeRange: ShopCommand.OpenTimeRange)
     * 파라미터로 전달된 shop의 오픈 시간, 종료 시간을 변경해주는 메소드
     * @param shop 가게 entity
     * @param openTimeRange to-be if it is not-null
     */
    private fun changeOpenTimeRange(shop: Shop, openTimeRange: ShopCommand.OpenTimeRange?): Shop {
        return when (openTimeRange) {
            null -> shop
            else -> shop.changeOpenCloseTime(
                openTimeRange.openTime,
                openTimeRange.closeTime
            )
        }
    }
}