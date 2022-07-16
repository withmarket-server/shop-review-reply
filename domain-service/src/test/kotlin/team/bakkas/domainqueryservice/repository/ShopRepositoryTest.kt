package team.bakkas.domainqueryservice.repository

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.CoroutinesUtils
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.util.StopWatch
import reactor.core.publisher.Mono
import team.bakkas.domaindynamo.entity.Shop
import team.bakkas.domaindynamo.repository.ShopDynamoRepository
import java.time.Duration

@SpringBootTest
internal class ShopRepositoryTest @Autowired constructor(
    val shopDynamoRepository: ShopDynamoRepository,
    val shopRepository: ShopRepository,
    val shopReactiveRedisTemplate: ReactiveRedisTemplate<String, Shop>
) {

    @ParameterizedTest
    @CsvSource(value = ["33daf043-7f36-4a52-b791-018f9d5eb218:역전할머니맥주 영남대점"], delimiter = ':')
    @DisplayName("Cache hit이 성공하는지 테스트하는 메소드")
    fun findOneShopCachingSuccess1(shopId: String, shopName: String): Unit = runBlocking {
        // given
        val key = generateKey(shopId, shopName)
        val alternativeShopMono: Mono<Shop?> = shopDynamoRepository.findShopByIdAndNameAsync(shopId, shopName)
            .doOnSuccess {
                it?.let {
                    shopReactiveRedisTemplate.opsForValue().set(key, it, Duration.ofDays(1L))
                        .subscribe()
                }
            }.onErrorResume {
                Mono.empty()
            }
        val resultMono: Mono<Shop> = shopReactiveRedisTemplate.opsForValue().get(key)
            .switchIfEmpty(alternativeShopMono)

        // when
        val shopDeferred = CoroutinesUtils.monoToDeferred(resultMono) // Mono를 Coroutine으로 변환
        val resultShop = shopDeferred.await() // Mono로부터 결과를 얻을 때까지 Coroutine을 block

        // redis로부터 결과를 받아오기
        val redisShopMono: Mono<Shop> = shopReactiveRedisTemplate.opsForValue().get(key)
        val redisShop = CoroutinesUtils.monoToDeferred(redisShopMono).await()

        // then
        assertNotNull(resultShop)
        resultShop?.let {
            assertEquals(it.shopId, shopId)
            assertEquals(it.shopName, shopName)
        }

        assertNotNull(redisShop)
        redisShop?.let {
            assertEquals(it.shopId, shopId)
            assertEquals(it.shopName, shopName)
        }

        println(resultShop)
    }

    @ParameterizedTest
    @CsvSource(value = ["33daf043-7f36-4a52-b791-018f9d5eb218:역전할머니맥주 페이크"], delimiter = ':')
    @DisplayName("잘못된 shopName으로 인해서 아이템을 가져오지 못하는 테스트")
    fun findOneShopCachingFail1(shopId: String, shopName: String): Unit = runBlocking {
        // given
        val key = generateKey(shopId, shopName)
        val alternativeShopMono: Mono<Shop?> = shopDynamoRepository.findShopByIdAndNameAsync(shopId, shopName)
            .doOnSuccess {
                it?.let {
                    shopReactiveRedisTemplate.opsForValue().set(key, it, Duration.ofDays(1L))
                        .subscribe()
                }
            }.onErrorResume {
                Mono.empty()
            }

        // redis에서 key를 통해서 가게를 못 찾아내는 경우 slternative mono를 실행한다
        val resultMono: Mono<Shop> = shopReactiveRedisTemplate.opsForValue().get(key)
            .switchIfEmpty(alternativeShopMono)

        // when
        val shopDeferred = CoroutinesUtils.monoToDeferred(resultMono)
        val resultShop = shopDeferred.await()

        // then
        assertNull(resultShop)

        println("Test passed!!")
    }

    @ParameterizedTest
    @CsvSource(value = ["xxxxxxx-7f36-4a52-b791-018f9d5eb218:역전할머니맥주 영남대점"], delimiter = ':')
    @DisplayName("ShopId가 잘못되어서 가게를 찾지 못하는 케이스 테스트")
    fun findOneShopCachingFail2(shopId: String, shopName: String): Unit = runBlocking {
        // given
        val key = generateKey(shopId, shopName)
        val alternativeShopMono: Mono<Shop?> = shopDynamoRepository.findShopByIdAndNameAsync(shopId, shopName)
            .doOnSuccess {
                it?.let {
                    shopReactiveRedisTemplate.opsForValue().set(key, it, Duration.ofDays(1L))
                        .subscribe()
                }
            }.onErrorResume {
                Mono.empty()
            }
        val resultMono = shopReactiveRedisTemplate.opsForValue().get(key)
            .switchIfEmpty(alternativeShopMono)

        // when
        val shopDeferred: Deferred<Shop?> = CoroutinesUtils.monoToDeferred(resultMono)
        val resultShop: Shop? = shopDeferred.await()

        val redisShopMono: Mono<Shop?> = shopReactiveRedisTemplate.opsForValue().get(key)
        val redisShopDeferred = CoroutinesUtils.monoToDeferred(redisShopMono)
        val shopFromRedis = redisShopDeferred.await()

        // then
        assertNull(resultShop)
        assertNull(shopFromRedis)

        println("Test passed!!")
    }

    @ParameterizedTest
    @CsvSource(value = ["xxxxxxx-7f36-4a52-b791-018f9d5eb218:역전할머니맥주 영남대점"], delimiter = ':')
    @DisplayName("[Repository Test] ShopId가 잘못되어서 가게를 찾지 못하는 케이스 테스트")
    fun testFindOneShopFail1(shopId: String, shopName: String): Unit = runBlocking {
        // given
        val key = generateKey(shopId, shopName)
        val shopMono: Mono<Shop?> = shopRepository.findShopByIdAndNameWithCaching(shopId, shopName)

        // when
        val result = CoroutinesUtils.monoToDeferred(shopMono).await()
        val redisResult = CoroutinesUtils.monoToDeferred(shopReactiveRedisTemplate.opsForValue().get(key))
            .await()

        // then
        assertNull(result)
        assertNull(redisResult)

        println("Test passed!!")
    }

    @ParameterizedTest
    @CsvSource(value = ["33daf043-7f36-4a52-b791-018f9d5eb218:역전할머니맥주 페이크"], delimiter = ':')
    @DisplayName("[Repository Test] 잘못된 shopName으로 인해서 아이템을 가져오지 못하는 테스트")
    fun testFindOneShopFail2(shopId: String, shopName: String): Unit = runBlocking {
        // given
        val key = generateKey(shopId, shopName)
        val shopMono: Mono<Shop?> = shopRepository.findShopByIdAndNameWithCaching(shopId, shopName)

        // when
        val result = CoroutinesUtils.monoToDeferred(shopMono).await()
        val redisResult = CoroutinesUtils.monoToDeferred(shopReactiveRedisTemplate.opsForValue().get(key))
            .await()

        // then
        assertNull(result)
        assertNull(redisResult)

        println("Test passed!!")
    }

    @ParameterizedTest
    @CsvSource(value = ["33daf043-7f36-4a52-b791-018f9d5eb218:역전할머니맥주 영남대점"], delimiter = ':')
    @DisplayName("[Repository Test] Cache hit이 정상 동작하면서 아이템을 가져오는지 테스트")
    fun testFindOneShopSuccess1(shopId: String, shopName: String): Unit = runBlocking {
        // given
        val key = generateKey(shopId, shopName)
        val shopMono: Mono<Shop?> = shopRepository.findShopByIdAndNameWithCaching(shopId, shopName)

        // when
        val result = CoroutinesUtils.monoToDeferred(shopMono).await()
        val redisResult = CoroutinesUtils.monoToDeferred(shopReactiveRedisTemplate.opsForValue().get(key))
            .await()

        // then
        assertNotNull(result)
        result?.let {
            assertEquals(it.shopId, shopId)
            assertEquals(it.shopName, shopName)
        }

        assertNotNull(redisResult)
        redisResult?.let {
            assertEquals(it.shopId, shopId)
            assertEquals(it.shopName, shopName)
        }
    }

    @ParameterizedTest
    @CsvSource(value = ["33daf043-7f36-4a52-b791-018f9d5eb218:역전할머니맥주 영남대점"], delimiter = ':')
    @DisplayName("[Repository Test] Cache hit 성능 비교")
    fun compareCaching(shopId: String, shopName: String): Unit = runBlocking {
        val stopWatch = StopWatch()
        val normalAsyncResult = mutableListOf<Shop?>() // 일반적인 비동기로 처리했을 때의 속도
        val cachingAsyncResult = mutableListOf<Shop?>() // 캐시 히팅을 통해서 비동기 처리했을 때의 속도

        stopWatch.start()
        val normalJob = launch {
            repeat(100) {
                val shopMono: Mono<Shop?> = shopDynamoRepository.findShopByIdAndNameAsync(shopId, shopName)
                val shopDeferred = CoroutinesUtils.monoToDeferred(shopMono)
                normalAsyncResult.add(shopDeferred.await())
            }
        }

        normalJob.join()
        stopWatch.stop()

        val normalSpeed = stopWatch.totalTimeMillis

        println("normal async: $normalSpeed") // 3306 mills

        stopWatch.start()
        val cachingJob = launch {
            repeat(100) {
                val shopMono: Mono<Shop?> = shopRepository.findShopByIdAndNameWithCaching(shopId, shopName)
                val shopDeferred = CoroutinesUtils.monoToDeferred(shopMono)
                cachingAsyncResult.add(shopDeferred.await())
            }
        }

        cachingJob.join()
        stopWatch.stop()

        val cachingSpeed = stopWatch.totalTimeMillis - normalSpeed

        println("caching async: $cachingSpeed") // 1780 mills
    }

    // Cache hit 방식으로 모든 shop을 가져오는 테스트
    @Test
    @DisplayName("Cache hit 방식을 이용해서 모든 shop을 가져오기")
    fun getAllShopFlow(): Unit = runBlocking {
        // given
        val shopKeysFlow = shopDynamoRepository.getAllShopKeys()
        val cachedShopFlow = shopKeysFlow.map {
            shopRepository.findShopByIdAndNameWithCaching(it.first, it.second)
        }

        // when
        val shopList = mutableListOf<Shop>()
        cachedShopFlow.buffer()
            .collect { shopMono ->
                val shopDeferred = CoroutinesUtils.monoToDeferred(shopMono)
                shopList.add(shopDeferred.await()!!)
            }

        // then
        val redisShopList = mutableListOf<Shop>()
        shopList.forEach {
            val redisShopMono = shopReactiveRedisTemplate.opsForValue().get(generateKey(it.shopId, it.shopName))
            redisShopList.add(CoroutinesUtils.monoToDeferred(redisShopMono).await())
        }

        assert(shopList.size != 0)
        assert(redisShopList.size != 0)
        assertEquals(shopList.size, redisShopList.size)
        shopList.zip(redisShopList).forEach {
            assertEquals(it.first.shopId, it.second.shopId)
            assertEquals(it.first.shopName, it.second.shopName)
        }

        shopList.forEach {
            println(it)
        }
    }

    @Test
    @DisplayName("[repository] 작성한 getAllShopWithCaching 검증")
    fun getAllShopWithCaching(): Unit = runBlocking {
        // given
        val shopFlow = shopRepository.getAllShopsWithCaching()
        val shopList = mutableListOf<Shop>()

        // when
        shopFlow.buffer()
            .collect {
                val shopDeferred = CoroutinesUtils.monoToDeferred(it)
                val shop = withContext(Dispatchers.IO) {
                    shopDeferred.await()!!
                }
                shopList.add(shop)
            }

        // then
        val redisShopList = mutableListOf<Shop>()
        shopDynamoRepository.getAllShopKeys().map {
            generateKey(it.first, it.second)
        }.buffer()
            .collect {
                val shopMono = shopReactiveRedisTemplate.opsForValue().get(it)
                val shop = CoroutinesUtils.monoToDeferred(shopMono).await()
                redisShopList.add(shop)
            }

        assert(shopList.size != 0)
        assert(redisShopList.size != 0)
        assertEquals(shopList.size, redisShopList.size)
        shopList.zip(redisShopList).forEach {
            assertEquals(it.first.shopId, it.second.shopId)
            assertEquals(it.first.shopName, it.second.shopName)
        }

        println("Test passed!!")
        shopList.forEach {
            println(it)
        }
    }

    private fun generateKey(shopId: String, shopName: String): String = "shop:${shopId}-${shopName}"
}