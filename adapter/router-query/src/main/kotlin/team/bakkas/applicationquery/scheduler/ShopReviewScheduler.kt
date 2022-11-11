package team.bakkas.applicationquery.scheduler

import kotlinx.coroutines.reactor.asFlux
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import team.bakkas.domainquery.reader.ifs.ShopReader
import team.bakkas.domainquery.reader.ifs.ShopReviewReader

/**
 * @author Doyeop Kim
 * @since 2022/11/11
 */
@Component
class ShopReviewScheduler(
    private val shopReader: ShopReader,
    private val shopReviewReader: ShopReviewReader
) {

    // 매 시간 정각마다 데이터 싱크를 맞춰주는 메소드
    @Scheduled(cron = "0 0 * * * *")
    fun cacheShopReviews() {
        shopReader.getAllShopsWithCaching()
            .asFlux()
            .doOnNext { shopReviewReader.getReviewsOfShopWithCaching(it.shopId) }
            .log()
            .subscribe()
    }
}