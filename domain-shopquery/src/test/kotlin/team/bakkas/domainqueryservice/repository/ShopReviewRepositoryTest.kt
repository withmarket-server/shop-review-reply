package team.bakkas.domainqueryservice.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
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
import team.bakkas.domaindynamo.repository.dynamo.ShopReviewDynamoRepositoryImpl
import java.time.Duration

@SpringBootTest
internal class ShopReviewRepositoryTest @Autowired constructor(
    val shopReviewDynamoRepository: ShopReviewDynamoRepositoryImpl,
    val shopReviewRepository: ShopReviewRepository,
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
        val alternativeMono = shopReviewDynamoRepository.findReviewByIdAndTitleAsync(reviewId, reviewTitle)
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
        val alternativeMono = shopReviewDynamoRepository.findReviewByIdAndTitleAsync(reviewId, reviewTitle)
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
        val alternativeMono = shopReviewDynamoRepository.findReviewByIdAndTitleAsync(reviewId, reviewTitle)
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
        val reviewMono = shopReviewRepository.findShopReviewByIdAndTitleWithCaching(reviewId, reviewTitle)
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
        val reviewMono = shopReviewRepository.findShopReviewByIdAndTitleWithCaching(reviewId, reviewTitle)
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
        val reviewMono = shopReviewRepository.findShopReviewByIdAndTitleWithCaching(reviewId, reviewTitle)
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
                val shopReviewMono = shopReviewDynamoRepository.findReviewByIdAndTitleAsync(reviewId, reviewTitle)
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
                val shopReviewMono = shopReviewRepository.findShopReviewByIdAndTitleWithCaching(reviewId, reviewTitle)
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

    @ParameterizedTest
    @CsvSource(value = ["33daf043-7f36-4a52-b791-018f9d5eb218:역전할머니맥주 영남대점"], delimiter = ':')
    @DisplayName("Cache hit을 하면서 review를 가져오는지 테스트")
    fun getAllReviewsByShopIdAndNameSuccess1(shopId: String, shopName: String): Unit = runBlocking {
        // given
        val reviewKeysFlow = shopReviewDynamoRepository.getAllReviewKeyFlowByShopIdAndName(shopId, shopName)
        val reviewList = mutableListOf<ShopReview>()

        // when
        reviewKeysFlow.map {
            shopReviewRepository.findShopReviewByIdAndTitleWithCaching(it.first, it.second)
        }.buffer()
            .collect { shopReviewMono ->
                val reviewDeferred = CoroutinesUtils.monoToDeferred(shopReviewMono)
                reviewList.add(reviewDeferred.await()!!)
            }

        // then
        val redisReviewList = mutableListOf<ShopReview>()
        reviewKeysFlow.map {
            val redisKey = generateRedisKey(it.first, it.second)
            val redisReviewMono = shopReviewReactiveRedisTemplate.opsForValue().get(redisKey)
            CoroutinesUtils.monoToDeferred(redisReviewMono)
        }.buffer()
            .collect {
                redisReviewList.add(it.await()!!)
            }

        assert(reviewList.size != 0)
        assert(redisReviewList.size != 0)
        assertEquals(reviewList.size, redisReviewList.size)
        reviewList.zip(redisReviewList).forEach {
            assertEquals(it.first.shopId, it.second.shopId)
            assertEquals(it.first.shopName, it.second.shopName)
            assertEquals(it.first.reviewId, it.second.reviewId)
            assertEquals(it.first.reviewTitle, it.second.reviewTitle)
        }

        println("Test passed!!")
        println(reviewList)
    }

    @ParameterizedTest
    @CsvSource(value = ["xxxxxxxx-7f36-4a52-b791-018f9d5eb218:역전할머니맥주 영남대점"], delimiter = ':')
    @DisplayName("잘못된 shopId가 전달되어 하나도 못 가져오는 경우 테스트")
    fun getAllReviewsByShopIdAndNameFail1(shopId: String, shopName: String): Unit = runBlocking {
        // given
        val reviewKeysFlow = shopReviewDynamoRepository.getAllReviewKeyFlowByShopIdAndName(shopId, shopName)
        val reviewList = mutableListOf<ShopReview>()

        // when
        reviewKeysFlow.map {
            shopReviewRepository.findShopReviewByIdAndTitleWithCaching(it.first, it.second)
        }.buffer()
            .collect { shopReviewMono ->
                val reviewDeferred = CoroutinesUtils.monoToDeferred(shopReviewMono)
                reviewList.add(reviewDeferred.await()!!)
            }

        // then
        val redisReviewList = mutableListOf<ShopReview>()
        reviewKeysFlow.map {
            val redisKey = generateRedisKey(it.first, it.second)
            val redisReviewMono = shopReviewReactiveRedisTemplate.opsForValue().get(redisKey)
            CoroutinesUtils.monoToDeferred(redisReviewMono)
        }.buffer()
            .collect {
                redisReviewList.add(it.await()!!)
            }

        assert(reviewList.size == 0)
        assert(redisReviewList.size == 0)
        assertEquals(reviewList.size, redisReviewList.size)

        println("Test passed!!")
    }

    @ParameterizedTest
    @CsvSource(value = ["33daf043-7f36-4a52-b791-018f9d5eb218:역전할머니맥주 가짜"], delimiter = ':')
    @DisplayName("잘못된 shopName이 전달되서 review를 못 가져오는 경우 테스트")
    fun getAllReviewsByShopIdAndNameFail2(shopId: String, shopName: String): Unit = runBlocking {
        // given
        val reviewKeysFlow = shopReviewDynamoRepository.getAllReviewKeyFlowByShopIdAndName(shopId, shopName)
        val reviewList = mutableListOf<ShopReview>()

        // when
        reviewKeysFlow.map {
            shopReviewRepository.findShopReviewByIdAndTitleWithCaching(it.first, it.second)
        }.buffer()
            .collect { shopReviewMono ->
                val reviewDeferred = CoroutinesUtils.monoToDeferred(shopReviewMono)
                reviewList.add(reviewDeferred.await()!!)
            }

        // then
        val redisReviewList = mutableListOf<ShopReview>()
        reviewKeysFlow.map {
            val redisKey = generateRedisKey(it.first, it.second)
            val redisReviewMono = shopReviewReactiveRedisTemplate.opsForValue().get(redisKey)
            CoroutinesUtils.monoToDeferred(redisReviewMono)
        }.buffer()
            .collect {
                redisReviewList.add(it.await()!!)
            }

        assert(reviewList.size == 0)
        assert(redisReviewList.size == 0)

        println("Test passed!!")
    }

    @ParameterizedTest
    @CsvSource(value = ["33daf043-7f36-4a52-b791-018f9d5eb218:역전할머니맥주 영남대점"], delimiter = ':')
    @DisplayName("[repository] Cache hit을 하면서 review를 가져오는지 테스트")
    fun testGetReviewListFlowByShopIdAndNameSuccess1(shopId: String, shopName: String): Unit = runBlocking {
        // given
        val reviewFlow = shopReviewRepository.getShopReviewListFlowByShopIdAndNameWithCaching(shopId, shopName)
        val reviewList = mutableListOf<ShopReview>()

        // when
        reviewFlow.map {
            CoroutinesUtils.monoToDeferred(it)
        }.buffer()
            .collect {
                withContext(Dispatchers.IO) {
                    reviewList.add(it.await()!!)
                }
            }

        // then
        val redisReviewList = mutableListOf<ShopReview>()
        val keyFlow = shopReviewDynamoRepository.getAllReviewKeyFlowByShopIdAndName(shopId, shopName)
        keyFlow.map {
            val redisKey = generateRedisKey(it.first, it.second)
            val reviewMono = shopReviewReactiveRedisTemplate.opsForValue().get(redisKey)
            CoroutinesUtils.monoToDeferred(reviewMono)
        }.buffer()
            .collect {
                withContext(Dispatchers.IO) {
                    redisReviewList.add(it.await()!!)
                }
            }

        assert(reviewList.size != 0)
        assert(redisReviewList.size != 0)
        assertEquals(reviewList.size, redisReviewList.size)
        reviewList.zip(redisReviewList).forEach {
            assertEquals(it.first.shopId, it.second.shopId)
            assertEquals(it.first.shopName, it.second.shopName)
            assertEquals(it.first.reviewId, it.second.reviewId)
            assertEquals(it.first.reviewTitle, it.second.reviewTitle)
        }

        println("Test passed!!")
        reviewList.forEach {
            println(it)
        }
    }

    @ParameterizedTest
    @CsvSource(value = ["xxxxxxxx-7f36-4a52-b791-018f9d5eb218:역전할머니맥주 영남대점"], delimiter = ':')
    @DisplayName("[repository] shopId가 잘못되어 리뷰를 하나도 못 가져옴")
    fun testGetReviewListFlowByShopIdAndNameFail1(shopId: String, shopName: String): Unit = runBlocking {
        // given
        val reviewFlow = shopReviewRepository.getShopReviewListFlowByShopIdAndNameWithCaching(shopId, shopName)
        val reviewList = mutableListOf<ShopReview>()

        // when
        reviewFlow.map {
            CoroutinesUtils.monoToDeferred(it)
        }.buffer()
            .collect {
                withContext(Dispatchers.IO) {
                    reviewList.add(it.await()!!)
                }
            }

        // then
        val redisReviewList = mutableListOf<ShopReview>()
        val keyFlow = shopReviewDynamoRepository.getAllReviewKeyFlowByShopIdAndName(shopId, shopName)
        keyFlow.map {
            val redisKey = generateRedisKey(it.first, it.second)
            val reviewMono = shopReviewReactiveRedisTemplate.opsForValue().get(redisKey)
            CoroutinesUtils.monoToDeferred(reviewMono)
        }.buffer()
            .collect {
                redisReviewList.add(it.await()!!)
            }

        assert(reviewList.size == 0)
        assert(redisReviewList.size == 0)

        println("Test passed!!")
    }

    @ParameterizedTest
    @CsvSource(value = ["33daf043-7f36-4a52-b791-018f9d5eb218:역전할머니맥주 가짜"], delimiter = ':')
    @DisplayName("[repository] shopName이 잘못되어 리뷰를 하나도 못 가져옴")
    fun testGetReviewListFlowByShopIdAndNameFail2(shopId: String, shopName: String): Unit = runBlocking {
        // given
        val reviewFlow = shopReviewRepository.getShopReviewListFlowByShopIdAndNameWithCaching(shopId, shopName)
        val reviewList = mutableListOf<ShopReview>()

        // when
        reviewFlow.map {
            CoroutinesUtils.monoToDeferred(it)
        }.buffer()
            .collect {
                withContext(Dispatchers.IO) {
                    reviewList.add(it.await()!!)
                }
            }

        // then
        val redisReviewList = mutableListOf<ShopReview>()
        val keyFlow = shopReviewDynamoRepository.getAllReviewKeyFlowByShopIdAndName(shopId, shopName)
        keyFlow.map {
            val redisKey = generateRedisKey(it.first, it.second)
            val reviewMono = shopReviewReactiveRedisTemplate.opsForValue().get(redisKey)
            CoroutinesUtils.monoToDeferred(reviewMono)
        }.buffer()
            .collect {
                redisReviewList.add(it.await()!!)
            }

        assert(reviewList.size == 0)
        assert(redisReviewList.size == 0)

        println("Test passed!!")
    }
}