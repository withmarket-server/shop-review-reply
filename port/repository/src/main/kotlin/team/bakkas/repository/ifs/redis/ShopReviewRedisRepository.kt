package team.bakkas.repository.ifs.redis

import kotlinx.coroutines.flow.Flow
import reactor.core.publisher.Mono
import team.bakkas.domaindynamo.entity.ShopReview

interface ShopReviewRedisRepository {

    // review를 캐싱하는 메소드
    fun cacheReview(shopReview: ShopReview): Mono<Boolean>

    // review를 key를 기반으로 가져오는 메소드
    fun findReviewByKey(reviewKey: String): Mono<ShopReview>

    // review를 삭제하는 메소드
    fun deleteReview(shopReview: ShopReview): Mono<Boolean>

    // shopId, shopName의 정보를 이용하여 해당 shop의 모든 review를 가져오는 메소드
    fun getShopReviewFlowByShopIdAndName(shopId: String, shopName: String): Flow<ShopReview>
}