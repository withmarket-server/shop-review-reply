package team.bakkas.applicationcommand.handler

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.common.runBlocking
import io.mockk.coEvery
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.spyk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.mock.web.reactive.function.server.MockServerRequest
import reactor.core.publisher.Mono
import team.bakkas.applicationcommand.grpc.ifs.ShopGrpcClient
import team.bakkas.applicationcommand.grpc.ifs.ShopReviewGrpcClient
import team.bakkas.applicationcommand.validator.ShopReviewValidatorImpl
import team.bakkas.clientcommand.shopReview.ShopReviewCommand
import team.bakkas.common.exceptions.RequestFieldException
import team.bakkas.common.exceptions.RequestParamLostException
import team.bakkas.common.exceptions.shop.ShopNotFoundException
import team.bakkas.common.exceptions.shopReview.ShopReviewNotFoundException
import team.bakkas.eventinterface.eventProducer.ShopReviewEventProducer
import team.bakkas.grpcIfs.v1.shop.CheckExistShopResponse
import team.bakkas.grpcIfs.v1.shopReview.CheckExistShopReviewResponse
import team.bakkas.servicecommand.validator.ShopReviewValidator

@ExtendWith(MockKExtension::class)
internal class ShopReviewCommandHandlerUnitTest {
    private lateinit var shopReviewCommandHandler: ShopReviewCommandHandler

    private lateinit var shopReviewValidator: ShopReviewValidator

    private lateinit var shopGrpcClient: ShopGrpcClient

    private lateinit var shopReviewGrpcClient: ShopReviewGrpcClient

    private lateinit var shopReviewEventProducer: ShopReviewEventProducer

    @BeforeEach
    fun setUp() {
        shopGrpcClient = mockk()
        shopReviewGrpcClient = mockk()
        shopReviewValidator = spyk(ShopReviewValidatorImpl(shopGrpcClient, shopReviewGrpcClient))
        shopReviewEventProducer = mockk()
        shopReviewCommandHandler = spyk(ShopReviewCommandHandler(shopReviewValidator, shopReviewEventProducer))
    }

    @Test
    @DisplayName("[createReview] 1. reviewTitle이 비어서 들어오는 경우 RequestFieldException을 일으키는 테스트")
    fun createReviewTest1(): Unit = runBlocking {
        // given
        val requestBody = generateCreateRequest().apply {
            reviewTitle = ""
        }
        val request = generateRequest { Mono.just(requestBody) }

        // when and then
        shouldThrow<RequestFieldException> { shopReviewCommandHandler.createReview(request) }
    }

    @Test
    @DisplayName("[createReview] 2. shopId가 비어서 들어오는 경우 RequestFieldException을 일으키는 테스트")
    fun createReviewTest2(): Unit = runBlocking {
        val requestBody = generateCreateRequest().apply {
            shopId = ""
        }
        val request = generateRequest { Mono.just(requestBody) }

        // then
        shouldThrow<RequestFieldException> { shopReviewCommandHandler.createReview(request) }
    }

    @Test
    @DisplayName("[createReview] 3. shopName가 비어서 들어오는 경우 RequestFieldException을 일으키는 테스트")
    fun createReviewTest3(): Unit = runBlocking {
        val requestBody = generateCreateRequest().apply {
            shopName = ""
        }
        val request = generateRequest { Mono.just(requestBody) }

        // then
        shouldThrow<RequestFieldException> { shopReviewCommandHandler.createReview(request) }
    }

    @Test
    @DisplayName("[createReview] 4. reviewContent가 비어서 들어오는 경우 RequestFieldException을 일으키는 테스트")
    fun createReviewTest4(): Unit = runBlocking {
        val requestBody = generateCreateRequest().apply {
            reviewContent= ""
        }
        val request = generateRequest { Mono.just(requestBody) }

        // then
        shouldThrow<RequestFieldException> { shopReviewCommandHandler.createReview(request) }
    }

    @Test
    @DisplayName("[createReview] 5. reviewContent가 200자 넘게 들어오는 경우 RequestFieldException을 일으키는 테스트")
    fun createReviewTest5(): Unit = runBlocking {
        // when
        val requestBody = generateCreateRequest().apply {
            reviewContent = createContent("a", 201)
        }
        val request = generateRequest { Mono.just(requestBody) }

        // then
        shouldThrow<RequestFieldException> { shopReviewCommandHandler.createReview(request) }
    }

    @Test
    @DisplayName("[createReview] 6. reviewScore가 음수여서 RequestFieldException을 일으키는 테스트")
    fun createReviewTest6(): Unit = runBlocking {
        // when
        val requestBody = generateCreateRequest().apply {
            reviewScore = -0.1
        }
        val request = generateRequest { Mono.just(requestBody) }

        // then
        shouldThrow<RequestFieldException> { shopReviewCommandHandler.createReview(request) }
    }

    @Test
    @DisplayName("[createReview] 7. reviewScore가 10 초과여서 RequestFieldException을 일으키는 테스트")
    fun createReviewTest7(): Unit = runBlocking {
        // when
        val requestBody = generateCreateRequest().apply {
            reviewScore = 10.1
        }
        val request = generateRequest { Mono.just(requestBody) }

        // then
        shouldThrow<RequestFieldException> { shopReviewCommandHandler.createReview(request) }
    }

    @Test
    @DisplayName("[createReview] 8. shop이 존재하지 않아서 ShopNotFoundException을 일으키는 테스트")
    fun createReviewTest8(): Unit = runBlocking {
        // given
        val requestBody = generateCreateRequest()
        val request = generateRequest { Mono.just(requestBody) }

        // request
        coEvery { shopGrpcClient.isExistShop(requestBody.shopId, requestBody.shopName) } returns
                CheckExistShopResponse.newBuilder()
                    .setResult(false).build()

        // then
        shouldThrow<ShopNotFoundException> { shopReviewCommandHandler.createReview(request) }
    }

    @Test
    @DisplayName("[deleteReview] 1. reviewId가 비어서 들어오는 경우 RequestParamLostException을 일으키는 테스트")
    fun deleteReviewTest1(): Unit = runBlocking {
        // given
        val id = ""
        val title = "mock title"
        val request = MockServerRequest.builder()
            .queryParam("id", id)
            .queryParam("title", title)
            .build()

        // then
        shouldThrow<RequestParamLostException> { shopReviewCommandHandler.deleteReview(request) }
    }

    @Test
    @DisplayName("[deleteReview] 2. reviewTitle이 비어서 들어오는 경우 RequestParamLostException을 일으키는 테스트")
    fun deleteReviewTest2(): Unit = runBlocking {
        // given
        val id = "fake id"
        val title = ""
        val request = MockServerRequest.builder()
            .queryParam("id", id)
            .queryParam("title", title)
            .build()

        // then
        shouldThrow<RequestParamLostException> { shopReviewCommandHandler.deleteReview(request) }
    }

    @Test
    @DisplayName("[deleteReview] 3. reviewId, reviewTitle이 모두 비어서 들어오는 경우 RequestParamLostException을 일으키는 테스트")
    fun deleteReviewTest3(): Unit = runBlocking {
        // given
        val id = ""
        val title = ""
        val request = MockServerRequest.builder()
            .queryParam("id", id)
            .queryParam("title", title)
            .build()

        // then
        shouldThrow<RequestParamLostException> { shopReviewCommandHandler.deleteReview(request) }
    }

    @Test
    @DisplayName("[deleteReview] 4. review가 존재하지 않는 경우 ShopReviewNotFoundException을 일으키는 테스트")
    fun deleteReviewTest4(): Unit = runBlocking {
        // given
        val id = "fake id"
        val title = "fake title"
        val request = MockServerRequest.builder()
            .queryParam("id", id)
            .queryParam("title", title)
            .build()

        // id, title에 대응하는 shopReview는 없다고 가정한다
        coEvery { shopReviewGrpcClient.isExistShopReview(id, title) } returns
                CheckExistShopReviewResponse.newBuilder()
                    .setResult(false)
                    .build()

        // then
        shouldThrow<ShopReviewNotFoundException> { shopReviewCommandHandler.deleteReview(request) }
    }

    // Create Request를 생성하는 메소드
    private fun generateCreateRequest(): ShopReviewCommand.CreateRequest = ShopReviewCommand.CreateRequest(
        reviewTitle = "가짜 리뷰",
        shopId = "test-001",
        shopName = "가짜 가게",
        reviewContent = "가짜 리뷰입니다. 속지 마세요!",
        reviewScore = 9.5,
        reviewPhotoList = listOf("가짜 사진")
    )

    // MockServerRequest를 생성하는 메소드
    private inline fun generateRequest(block: () -> Mono<Any>): MockServerRequest {
        return MockServerRequest.builder()
            .body(block())
    }

    private fun createContent(token: String, length: Int): String {
        val sb = java.lang.StringBuilder()

        for(i in 1..length) {
            sb.append(token)
        }

        return sb.toString()
    }
}