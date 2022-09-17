package team.bakkas.domainquery.repository

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.CoroutinesUtils
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.util.StopWatch
import reactor.core.publisher.Mono
import team.bakkas.domaindynamo.entity.ShopReview
import team.bakkas.repository.ifs.dynamo.ShopReviewDynamoRepository
import java.time.Duration

@SpringBootTest
internal class ShopReviewRepositoryTest @Autowired constructor(
    val shopReviewDynamoRepository: ShopReviewDynamoRepository,
    val shopReviewReader: ShopReviewReaderImpl,
    val shopReviewReactiveRedisTemplate: ReactiveRedisTemplate<String, ShopReview>
) {

    val DAYS_TO_LIVE = 1L

    // review에 대한 redis key를 생성하는 메소드
    private fun generateRedisKey(reviewId: String, reviewTitle: String) = "shopReview-$reviewId-$reviewTitle"

    @ParameterizedTest
    @CsvSource(value = ["xxxxxx-5120-4ec2-ab92-ca6827428945:진짜 최애 맥주집이에요!!"], delimiter = ':')
    @DisplayName("review id가 잘못되어 리뷰를 가져오지 못하는 테스트")
    fun findShopReviewWithCachingFail1(reviewId: String, reviewTitle: String): Unit = runBlocking {
        // given
        val key = generateRedisKey(reviewId, reviewTitle)
        // cache hit 결과 실패할 경우 대신 실행시킬 Mono 정의
        val alternativeMono = shopReviewDynamoRepository.findReviewByIdAndTitle(reviewId, reviewTitle)
            .doOnSuccess {
                it?.let {
                    shopReviewReactiveRedisTemplate.opsForValue().set(key, it, Duration.ofDays(DAYS_TO_LIVE))
                        .subscribe()
                }
            }.onErrorResume {
                Mono.empty()
            }
        val shopReviewMono = shopReviewReactiveRedisTemplate.opsForValue().get(key)
            .switchIfEmpty(alternativeMono)

        // when
        val reviewDeferred = CoroutinesUtils.monoToDeferred(shopReviewMono)
        val review = reviewDeferred.await()

        val redisReviewMono = shopReviewReactiveRedisTemplate.opsForValue().get(key)
        val redisReviewDeferred = CoroutinesUtils.monoToDeferred(redisReviewMono)
        val redisReview = redisReviewDeferred.await()

        // then
        assertNull(review)
        assertNull(redisReview)

        println("Test passed!!")
    }

    @ParameterizedTest
    @CsvSource(value = ["b7cbec2d-5120-4ec2-ab92-ca6827428945:진짜 최악의 맥주집이에요!!"], delimiter = ':')
    @DisplayName("review title이 잘못되어 리뷰를 가져오지 못하는 테스트")
    fun findShopReviewWithCachingFail2(reviewId: String, reviewTitle: String): Unit = runBlocking {
        // given
        val key = generateRedisKey(reviewId, reviewTitle)
        // cache hit 결과 실패할 경우 대신 실행시킬 Mono 정의
        val alternativeMono = shopReviewDynamoRepository.findReviewByIdAndTitle(reviewId, reviewTitle)
            .doOnSuccess {
                it?.let {
                    shopReviewReactiveRedisTemplate.opsForValue().set(key, it, Duration.ofDays(DAYS_TO_LIVE))
                        .subscribe()
                }
            }.onErrorResume {
                Mono.empty()
            }
        val shopReviewMono = shopReviewReactiveRedisTemplate.opsForValue().get(key)
            .switchIfEmpty(alternativeMono)

        // when
        val reviewDeferred = CoroutinesUtils.monoToDeferred(shopReviewMono)
        val review = reviewDeferred.await()

        val redisReviewMono = shopReviewReactiveRedisTemplate.opsForValue().get(key)
        val redisReviewDeferred = CoroutinesUtils.monoToDeferred(redisReviewMono)
        val redisReview = redisReviewDeferred.await()

        // then
        assertNull(review)
        assertNull(redisReview)

        println("Test passed!!")
    }

    @ParameterizedTest
    @CsvSource(value = ["b7cbec2d-5120-4ec2-ab92-ca6827428945:진짜 최애 맥주집이에요!!"], delimiter = ':')
    @DisplayName("정상적으로 리뷰를 잘 찾아오는 테스트")
    fun findShopReviewWithCachingSuccess1(reviewId: String, reviewTitle: String): Unit = runBlocking {
        // given
        val key = generateRedisKey(reviewId, reviewTitle)
        // cache hit 결과 실패할 경우 대신 실행시킬 Mono 정의
        val alternativeMono = shopReviewDynamoRepository.findReviewByIdAndTitle(reviewId, reviewTitle)
            .doOnSuccess {
                it?.let {
                    shopReviewReactiveRedisTemplate.opsForValue().set(key, it, Duration.ofDays(DAYS_TO_LIVE))
                        .subscribe()
                }
            }.onErrorResume {
                Mono.empty()
            }
        val shopReviewMono = shopReviewReactiveRedisTemplate.opsForValue().get(key)
            .switchIfEmpty(alternativeMono)

        // when
        val reviewDeferred = CoroutinesUtils.monoToDeferred(shopReviewMono)
        val review = reviewDeferred.await()

        val redisReviewMono = shopReviewReactiveRedisTemplate.opsForValue().get(key)
        val redisReviewDeferred = CoroutinesUtils.monoToDeferred(redisReviewMono)
        val redisReview = redisReviewDeferred.await()

        // then
        assertNotNull(review)
        review?.let {
            assertEquals(it.reviewId, reviewId)
            assertEquals(it.reviewTitle, reviewTitle)
        }

        assertNotNull(redisReview)
        redisReview?.let {
            assertEquals(it.reviewId, reviewId)
            assertEquals(it.reviewTitle, reviewTitle)
        }

        println(review)
        println("Test passed!!")
    }

    @ParameterizedTest
    @CsvSource(value = ["xxxxxx-5120-4ec2-ab92-ca6827428945:진짜 최애 맥주집이에요!!"], delimiter = ':')
    @DisplayName("[Repository] id가 틀려서 null이 반환")
    fun findShopReviewByIdAndReviewWithCachingFail1(reviewId: String, reviewTitle: String): Unit = runBlocking {
        // when
        val redisKey = generateRedisKey(reviewId, reviewTitle)
        val reviewMono = shopReviewReader.findReviewByIdAndTitle(reviewId, reviewTitle)
        val reviewDeferred = CoroutinesUtils.monoToDeferred(reviewMono)
        val review = reviewDeferred.await()

        // redis에 정확히 캐싱되었는지 검증
        val redisReviewMono = shopReviewReactiveRedisTemplate.opsForValue().get(redisKey)
        val redisReviewDeferred = CoroutinesUtils.monoToDeferred(redisReviewMono)
        val redisReview = redisReviewDeferred.await()

        // then
        assertNull(review)
        assertNull(redisReview)

        println("Test passed!!")
    }

    @ParameterizedTest
    @CsvSource(value = ["b7cbec2d-5120-4ec2-ab92-ca6827428945:진짜 최악의 맥주집이에요!!"], delimiter = ':')
    @DisplayName("[Repository] title이 틀려서 null이 반환")
    fun findShopReviewByIdAndReviewWithCachingFail2(reviewId: String, reviewTitle: String): Unit = runBlocking {
        // when
        val redisKey = generateRedisKey(reviewId, reviewTitle)
        val reviewMono = shopReviewReader.findReviewByIdAndTitle(reviewId, reviewTitle)
        val reviewDeferred = CoroutinesUtils.monoToDeferred(reviewMono)
        val review = reviewDeferred.await()

        // redis에 정확히 캐싱되었는지 검증
        val redisReviewMono = shopReviewReactiveRedisTemplate.opsForValue().get(redisKey)
        val redisReviewDeferred = CoroutinesUtils.monoToDeferred(redisReviewMono)
        val redisReview = redisReviewDeferred.await()

        // then
        assertNull(review)
        assertNull(redisReview)

        println("Test passed!!")
    }

    @ParameterizedTest
    @CsvSource(value = ["b7cbec2d-5120-4ec2-ab92-ca6827428945:진짜 최애 맥주집이에요!!"], delimiter = ':')
    @DisplayName("[Repository] 정상 동작")
    fun findShopReviewByIdAndReviewWithCachingSuccess1(reviewId: String, reviewTitle: String): Unit = runBlocking {
        // when
        val redisKey = generateRedisKey(reviewId, reviewTitle)
        val reviewMono = shopReviewReader.findReviewByIdAndTitle(reviewId, reviewTitle)
        val reviewDeferred = CoroutinesUtils.monoToDeferred(reviewMono)
        val review = reviewDeferred.await()

        // redis에 정확히 캐싱되었는지 검증
        val redisReviewMono = shopReviewReactiveRedisTemplate.opsForValue().get(redisKey)
        val redisReviewDeferred = CoroutinesUtils.monoToDeferred(redisReviewMono)
        val redisReview = redisReviewDeferred.await()

        // then
        assertNotNull(review)
        review?.let {
            assertEquals(it.reviewId, reviewId)
            assertEquals(it.reviewTitle, reviewTitle)
        }

        assertNotNull(redisReview)
        redisReview?.let {
            assertEquals(it.reviewId, reviewId)
            assertEquals(it.reviewTitle, reviewTitle)
        }

        Pair(review!!, redisReview).let {
            val first = it.first
            val second = it.second

            assertEquals(first.reviewId, second.reviewId)
            assertEquals(first.reviewTitle, second.reviewTitle)
            assertEquals(first.reviewScore, second.reviewScore)
            assertEquals(first.shopId, second.shopId)
            assertEquals(first.shopName, second.shopName)
        }

        println(review)
        println("Test passed!!")
    }


    @ParameterizedTest
    @CsvSource(value = ["b7cbec2d-5120-4ec2-ab92-ca6827428945:진짜 최애 맥주집이에요!!"], delimiter = ':')
    @DisplayName("[Repository] 기존의 Async와 속도 비교")
    fun compareFindingOneReview(reviewId: String, reviewTitle: String): Unit = runBlocking {
        val stopWatch = StopWatch()
        val beforeList = mutableListOf<ShopReview>()
        val afterList = mutableListOf<ShopReview>()

        stopWatch.start()
        val beforeJob = launch {
            repeat(100) {
                val shopReviewMono = shopReviewDynamoRepository.findReviewByIdAndTitle(reviewId, reviewTitle)
                val shopReview = CoroutinesUtils.monoToDeferred(shopReviewMono).await()
                beforeList.add(shopReview!!)
            }
        }
        beforeJob.join()
        stopWatch.stop()

        val beforeTime = stopWatch.totalTimeMillis

        stopWatch.start()
        val afterJob = launch {
            repeat(100) {
                val shopReviewMono = shopReviewReader.findReviewByIdAndTitle(reviewId, reviewTitle)
                val shopReview = CoroutinesUtils.monoToDeferred(shopReviewMono).await()
                afterList.add(shopReview!!)
            }
        }
        afterJob.join()
        stopWatch.stop()

        val afterTime = stopWatch.totalTimeMillis - beforeTime

        assert(beforeTime > afterTime)

        println("[Async normally]: $beforeTime") // 2965 mills
        println("[Async with caching]: $afterTime") // 1558 mills
        println("Test success!!")
    }
}