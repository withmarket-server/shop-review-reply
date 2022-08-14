package team.bakkas.domaindynamo.repository.redis

import reactor.core.publisher.Mono
import team.bakkas.domaindynamo.entity.ShopReview

interface ShopReviewRedisRepository {

    // review를 캐싱하는 메소드
    fun cacheReview(shopReview: ShopReview): Mono<Boolean>

    // review를 key를 기반으로 가져오는 메소드
    fun findReviewByKey(reviewKey: String): Mono<ShopReview>

    fun deleteReview(shopReview: ShopReview): Mono<Boolean>
}