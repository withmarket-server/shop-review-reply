package team.bakkas.domaindynamo.repository

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.CoroutinesUtils
import org.springframework.test.annotation.Rollback
import org.springframework.util.StopWatch
import reactor.core.publisher.Mono
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import team.bakkas.common.category.Category
import team.bakkas.common.category.DetailCategory
import team.bakkas.domaindynamo.entity.Shop
import team.bakkas.domaindynamo.repository.dynamo.ShopDynamoRepository
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@SpringBootTest
internal class ShopDynamoRepositoryTest @Autowired constructor(
    private val shopDynamoRepository: ShopDynamoRepository,
    private val dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient
) {

    @ParameterizedTest
    @CsvSource(value = ["ec1231-test001:포스마트:false"], delimiter = ':')
    @DisplayName("Shop 하나를 생성한다")
    @Rollback(value = false)
    fun createShop(shopId: String, shopName: String, isOpen: Boolean) {
        val mockShop = getMockShop(UUID.randomUUID().toString(), shopName, isOpen)

        val shop = shopDynamoRepository.createShop(mockShop)

        println(shop)
    }

    @ParameterizedTest
    @CsvSource(value = ["33daf043-7f36-4a52-b791-018f9d5eb218:역전할머니맥주 영남대점"], delimiter = ':')
    @DisplayName("Shop 하나를 찾아온다")
    fun findOneShop(shopId: String, shopName: String) {
        val foundShop = shopDynamoRepository.findShopByIdAndName(shopId, shopName)

        assertNotNull(foundShop)
        with(foundShop!!) {
            assertEquals(this.shopId, shopId)
            assertEquals(this.shopName, shopName)
        }

        println(foundShop)
        println("Test passed!!")
    }

    @ParameterizedTest
    @CsvSource(value = ["33daf043-7f36-4a52-b791-018f9d5eb218:역전할머니맥주 영남대점"], delimiter = ':')
    @DisplayName("dynamo에서 가져오는 것과 redis에서 가져오는 것의 속도 비교")
    fun compareFindOneShop(shopId: String, shopName: String) {
        val stopWatch = StopWatch()
        stopWatch.start()

        var foundShop = shopDynamoRepository.findShopByIdAndName(shopId, shopName)

        stopWatch.stop()

        println("shop을 가져오는데 걸린 시간: ${stopWatch.totalTimeSeconds}")

        assertNotNull(foundShop)
        with(foundShop!!) {
            assertEquals(this.shopId, shopId)
            assertEquals(this.shopName, shopName)
        }

        println("Test passed!!")
    }

    @Test
    @DisplayName("shop 테이블에 존재하는 모든 데이터를 긁어온다")
    fun 모든shop을가져온다() {
        val stopWatch = StopWatch()
        stopWatch.start()

        val shopList = shopDynamoRepository.findAllShop()

        stopWatch.stop()

        println("shopList을 가져오는데 걸린 시간: ${stopWatch.totalTimeSeconds}")

        shopList.forEach { shop -> println(shop) }
    }

    @ParameterizedTest
    @CsvSource(value = ["6b0999de-0bf1-4378-bd32-4ac808c2ae45:Hash"], delimiter = ':')
    fun deleteOneShop(shopId: String, shopName: String) {
        shopDynamoRepository.deleteShop(shopId, shopName)

        println("Test passed!!")
    }

    /* ==============================[Async Methods]============================== */
    @ParameterizedTest
    @CsvSource(value = ["33daf043-7f36-4a52-b791-018f9d5eb218:역전할머니맥주 영남대점"], delimiter = ':')
    @DisplayName("비동기로 아이템을 하나 가져온다")
    fun findOneShopAsync1(shopId: String, shopName: String): Unit = runBlocking {
        val table = dynamoDbEnhancedAsyncClient.table("shop", TableSchema.fromBean(Shop::class.java))
        val key = generateKey(shopId, shopName)
        val shopFuture = table.getItem(key)
        // kotlinx-coroutines-reactor로부터 확장 함수를 이용해서 Mono -> coroutines로 변환
        val shop = Mono.fromFuture(shopFuture).awaitSingleOrNull()

        assertNotNull(shop)
        shop?.let {
            assertEquals(it.shopId, shopId)
            assertEquals(it.shopName, shopName)
            println(it)
        }
    }

    @ParameterizedTest
    @CsvSource(value = ["33daf043-7f36-4a52-b791-018f9d5eb218:가짜할머니맥주 영남대점"], delimiter = ':')
    @DisplayName("비동기로 잘못된 이름으로 요청을 보내서 아이템을 가져오지 못한다")
    fun findOneShopAsync2(shopId: String, shopName: String): Unit = runBlocking {
        val table = dynamoDbEnhancedAsyncClient.table("shop", TableSchema.fromBean(Shop::class.java))
        val key = generateKey(shopId, shopName)
        val shopFuture = table.getItem(key)
        val shop = Mono.fromFuture(shopFuture).awaitSingleOrNull()

        assertNull(shop)

        println(shop)
    }

    @ParameterizedTest
    @CsvSource(value = ["33daf043-7f36-4a52-b791-018f9d5eb218:역전할머니맥주 영남대점"], delimiter = ':')
    @DisplayName("작성된 findShopByIdAndNameAsync 메소드의 성공 테스트")
    fun findShopByIdAndNameAsyncSuccess(shopId: String, shopName: String): Unit = runBlocking {
        val shopMono = shopDynamoRepository.findShopByIdAndNameAsync(shopId, shopName)
        val foundShop: Shop? = shopMono.awaitSingleOrNull()

        assertNotNull(foundShop)
        foundShop?.let {
            assertEquals(it.shopId, shopId)
            assertEquals(it.shopName, shopName)
        }

        println(foundShop)
    }

    @ParameterizedTest
    @CsvSource(value = ["33daf043-7f36-4a52-b791-018f9d5eb218:가짜할머니맥주 영남대점"], delimiter = ':')
    @DisplayName("작성된 findShopByIdAndNameAsync 메소드의 실패 테스트 (잘못된 가게 이름)")
    fun findShopByIdAndNameAsyncFail1(shopId: String, shopName: String): Unit = runBlocking {
        val shopMono = shopDynamoRepository.findShopByIdAndNameAsync(shopId, shopName)
        val foundShop: Shop? = shopMono.awaitSingleOrNull()

        assertNull(foundShop)

        println("Test passed!!")
    }

    @ParameterizedTest
    @CsvSource(value = ["xxxxxx-7f36-4a52-b791-018f9d5eb218:역전할머니맥주 영남대점"], delimiter = ':')
    @DisplayName("작성된 findShopByIdAndNameAsync 메소드의 실패 테스트(잘못된 shopId)")
    fun findShopByIdAndNameAsyncFail2(shopId: String, shopName: String): Unit = runBlocking {
        val shopMono = shopDynamoRepository.findShopByIdAndNameAsync(shopId, shopName)
        val foundShop: Shop? = shopMono.awaitSingleOrNull()

        assertNull(foundShop)

        println("Test passed!!")
    }

    // 모든 Shop을 받아오자
    @Test
    @DisplayName("모든 Shop list를 받아오는 테스트 (buffer 적용해서 코루틴 분리)")
    fun findAllShopSuccess1(): Unit = runBlocking {
        val table = dynamoDbEnhancedAsyncClient.table("shop", TableSchema.fromBean(Shop::class.java))
        val shopPublisher = table.scan().items()
        val shopFlow = shopPublisher.asFlow()
        val shopNameList = mutableListOf<String>()
        val stopWatch = StopWatch()

        stopWatch.start()
        shopFlow.map {shop ->
            shop.shopName
        }.buffer()
            .collect {
                shopNameList.add(it)
            }
        stopWatch.stop()


        println("Time: ${stopWatch.totalTimeMillis}") // 637, 779, 650 mills
        println(shopNameList)
    }

    @Test
    @DisplayName("모든 Shop list를 받아오는 테스트 (buffer 적용 없이 단일 코루틴으로 적용)")
    fun findAllShopSuccess2(): Unit = runBlocking {
        val table = dynamoDbEnhancedAsyncClient.table("shop", TableSchema.fromBean(Shop::class.java))
        val shopPublisher = table.scan().items()
        val shopFlow = shopPublisher.asFlow()
        val shopNameList = mutableListOf<String>()
        val stopWatch = StopWatch()

        stopWatch.start()
        shopFlow.map { shop -> shop.shopName }
            .collect { it -> shopNameList.add(it) }
        stopWatch.stop()

        println("Time: ${stopWatch.totalTimeMillis}") // 775, 632, 543 mills
        println(shopNameList)
    }

    @ParameterizedTest
    @CsvSource(value = ["ec5678-test002:나이스마트:false"], delimiter = ':')
    @DisplayName("Shop 하나를 생성한다")
    @Rollback(value = false)
    fun createShopAsync(shopId: String, shopName: String, isOpen: Boolean): Unit = runBlocking {
        val mockShop = getMockShop(UUID.randomUUID().toString(), shopName, isOpen)

        val createdShopMono = shopDynamoRepository.createShopAsync(mockShop)

        CoroutinesUtils.monoToDeferred(createdShopMono).await()
    }

    @ParameterizedTest
    @CsvSource(value = ["xxxxxxxx-ffa9-4ae3-ab84-f64307802c66:포스마트"], delimiter = ':')
    @DisplayName("Shop 하나를 제거하는데 실패한다")
    @Rollback(value = false)
    fun deleteShopAsyncFail(shopId: String, shopName: String): Unit = runBlocking {
        val deleteShopMono = shopDynamoRepository.deleteShopAsync(shopId, shopName)
        val deletedShop = CoroutinesUtils.monoToDeferred(deleteShopMono).await()

        assertNull(deletedShop)

        println("Test passed!!")
    }

    @ParameterizedTest
    @CsvSource(value = ["93e2b356-ffa9-4ae3-ab84-f64307802c66:포스마트"], delimiter = ':')
    @DisplayName("Shop 하나를 제거하는데 성공한다")
    fun deleteShopAsyncSuccess(shopId: String, shopName: String): Unit = runBlocking {
        val deleteShopMono = shopDynamoRepository.deleteShopAsync(shopId, shopName)
        val deletedShop = CoroutinesUtils.monoToDeferred(deleteShopMono).await()

        // then
        assertNotNull(deletedShop)
        with(deletedShop) {
            assertEquals(this.shopId, shopId)
            assertEquals(this.shopName, shopName)
        }

        println("Test passed!!")
        println(deletedShop)
    }

    fun generateKey(shopId: String, shopName: String) = Key.builder()
        .partitionValue(shopId)
        .sortValue(shopName)
        .build()

    fun getMockShop(shopId: String, shopName: String, isOpen: Boolean): Shop = Shop(
        shopId = shopId,
        shopName = shopName,
        isOpen = isOpen,
        openTime = LocalTime.now(),
        closeTime = LocalTime.now(),
        createdAt = LocalDateTime.now(),
        averageScore = 0.0,
        latitude = 35.838954,
        longitude = 128.755997,
        lotNumberAddress = "경상북도 경산시 북부동 305-13",
        roadNameAddress = "경상북도 경산시 대학로 321 1층",
        reviewNumber = 0,
        updatedAt = null,
        mainImage = "https://withmarket-image-bucket.s3.ap-northeast-2.amazonaws.com/c247bc62-e17f-43c1-90e9-60d566faaa3e.jpeg",
        representativeImageList = listOf("https://withmarket-image-bucket.s3.ap-northeast-2.amazonaws.com/c2570a85-1da7-4fec-9754-52a178e2abf5.jpeg"),
        isBranch = false,
        branchName = null,
        shopDescription = "삼겹살라면 존맛",
        shopCategory = Category.MART,
        shopDetailCategory = DetailCategory.SUPER_MARKET
    )
}