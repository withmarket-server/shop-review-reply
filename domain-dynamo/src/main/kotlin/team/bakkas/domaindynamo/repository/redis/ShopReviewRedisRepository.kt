package team.bakkas.domaindynamo.repository.redis

import reactor.core.publisher.Mono
import team.bakkas.domaindynamo.entity.ShopReview

interface ShopReviewRedisRepository {

    // review를 캐싱하는 메소드
    fun cacheReview(shopReview: ShopReview): Mono<Boolean>
}