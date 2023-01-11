package team.bakkas.applicationcommand.handler

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.common.runBlocking
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.spyk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.mock.web.reactive.function.server.MockServerRequest
import reactor.core.publisher.Mono
import team.bakkas.applicationcommand.grpc.ifs.ReplyGrpcClient
import team.bakkas.applicationcommand.grpc.ifs.ShopGrpcClient
import team.bakkas.applicationcommand.grpc.ifs.ShopReviewGrpcClient
import team.bakkas.applicationcommand.validator.ReplyValidatorImpl
import team.bakkas.clientcommand.reply.ReplyCommand
import team.bakkas.common.exceptions.RequestBodyLostException
import team.bakkas.common.exceptions.RequestFieldException
import team.bakkas.common.exceptions.RequestParamLostException
import team.bakkas.common.exceptions.reply.ReplyNotFoundException
import team.bakkas.common.exceptions.shop.MemberNotOwnerException
import team.bakkas.common.exceptions.shopReview.ShopReviewAlreadyRepliedException
import team.bakkas.common.exceptions.shopReview.ShopReviewNotFoundException
import team.bakkas.eventinterface.eventProducer.ReplyEventProducer
import team.bakkas.grpcIfs.v1.reply.CheckIsExistReplyResponse
import team.bakkas.grpcIfs.v1.shop.CheckIsOwnerOfShopResponse
import team.bakkas.grpcIfs.v1.shopReview.CheckExistShopReviewResponse
import team.bakkas.grpcIfs.v1.shopReview.CheckIsRepliedReviewResponse
import team.bakkas.servicecommand.validator.ReplyValidator

@ExtendWith(MockKExtension::class)
internal class ReplyCommandHandlerTest {
    private lateinit var replyCommandHandler: ReplyCommandHandler

    private lateinit var replyEventProducer: ReplyEventProducer

    private lateinit var replyValidator: ReplyValidator

    private lateinit var replyGrpcClient: ReplyGrpcClient

    private lateinit var shopReviewGrpcClient: ShopReviewGrpcClient

    private lateinit var shopGrpcClient: ShopGrpcClient

    @BeforeEach
    fun setUp() {
        replyGrpcClient = mockk(relaxed = true)
        shopReviewGrpcClient = mockk(relaxed = true)
        shopGrpcClient = mockk(relaxed = true)
        replyValidator = spyk(ReplyValidatorImpl(shopGrpcClient, shopReviewGrpcClient, replyGrpcClient))
        replyEventProducer = mockk(relaxed = true)
        replyCommandHandler = spyk(ReplyCommandHandler(replyValidator, replyEventProducer))
    }

    @Test
    @DisplayName("[createReply] 1. requestBody가 비어서 들어오는 경우")
    fun createReply1(): Unit = runBlocking {
        // given
        val request = createRequest { Mono.empty<ReplyCommand.CreateRequest>() }

        // then
        shouldThrow<RequestBodyLostException> { replyCommandHandler.createReply(request) }
    }

    @Test
    @DisplayName("[createReply] 2. memberId가 비어서 들어오는 경우 RequestFieldException을 일으키는 테스트")
    fun createReply2(): Unit = runBlocking {
        // given
        val dto = generateCreateRequest().apply {
            memberId = ""
        }
        val request = createRequest { Mono.just(dto) }

        // then
        shouldThrow<RequestFieldException> { replyCommandHandler.createReply(request) }
    }

    @Test
    @DisplayName("[createReply] 3. shopId가 비어서 들어오는 경우 RequestFieldException을 일으키는 테스트")
    fun createReply3(): Unit = runBlocking {
        // given
        val dto = generateCreateRequest().apply {
            shopId = ""
        }
        val request = createRequest { Mono.just(dto) }

        // then
        shouldThrow<RequestFieldException> { replyCommandHandler.createReply(request) }
    }

    @Test
    @DisplayName("[createReply] 4. reviewId가 비어서 들어오는 경우 RequestFieldException을 일으키는 테스트")
    fun createReply4(): Unit = runBlocking {
        // given
        val dto = generateCreateRequest().apply {
            reviewId = ""
        }
        val request = createRequest { Mono.just(dto) }

        // then
        shouldThrow<RequestFieldException> { replyCommandHandler.createReply(request) }
    }

    @Test
    @DisplayName("[createReply] 5. content가 비어서 들어오는 경우 RequestFieldException을 일으키는 테스트")
    fun createReply5(): Unit = runBlocking {
        // given
        val dto = generateCreateRequest().apply {
            content = ""
        }
        val request = createRequest { Mono.just(dto) }

        // then
        shouldThrow<RequestFieldException> { replyCommandHandler.createReply(request) }
    }

    @Test
    @DisplayName("[createReply] 6. Reply의 content가 200자 넘어서 들어오는 경우 RequestFieldException을 일으키는 테스트")
    fun createReply6(): Unit = runBlocking {
        // given
        val dto = generateCreateRequest().apply {
            content = getContent(201)
        }
        val request = createRequest { Mono.just(dto) }

        // then
        shouldThrow<RequestFieldException> { replyCommandHandler.createReply(request) }
    }

    @Test
    @DisplayName("[createReply] 7. 해당 shop의 주인이 member가 아닌 경우")
    fun createReply7(): Unit = runBlocking {
        // given
        val dto = generateCreateRequest()
        val request = createRequest { Mono.just(dto) }

        // when
        coEvery { shopGrpcClient.isOwnerOfShop(dto.memberId, dto.shopId) } returns
                CheckIsOwnerOfShopResponse.newBuilder()
                    .setResult(false)
                    .build()

        // then
        shouldThrow<MemberNotOwnerException> { replyCommandHandler.createReply(request) }
    }

    @Test
    @DisplayName("[createReply] 8. reviewId에 대응하는 review가 존재하지 않는 경우")
    fun createReply8(): Unit = runBlocking {
        // given
        val dto = generateCreateRequest()
        val request = createRequest { Mono.just(dto) }

        // when
        coEvery { shopGrpcClient.isOwnerOfShop(dto.memberId, dto.shopId) } returns
                CheckIsOwnerOfShopResponse.newBuilder()
                    .setResult(true)
                    .build()

        coEvery { shopReviewGrpcClient.isRepliedReview(dto.reviewId) } returns
                CheckIsRepliedReviewResponse.newBuilder()
                    .setIsExists(false)
                    .setIsReplied(false)
                    .build()

        // then
        shouldThrow<ShopReviewNotFoundException> { replyCommandHandler.createReply(request) }
    }

    @Test
    @DisplayName("[createReply] 9. 답글이 달린 review의 경우 ShopReviewAlreadyRepliedException을 반환한다")
    fun createReply9(): Unit = runBlocking {
        // given
        val dto = generateCreateRequest()
        val request = createRequest { Mono.just(dto) }

        // when
        coEvery { shopGrpcClient.isOwnerOfShop(dto.memberId, dto.shopId) } returns
                CheckIsOwnerOfShopResponse.newBuilder()
                    .setResult(true)
                    .build()

        coEvery { shopReviewGrpcClient.isRepliedReview(dto.reviewId) } returns
                CheckIsRepliedReviewResponse.newBuilder()
                    .setIsExists(true)
                    .setIsReplied(true)
                    .build()

        // then
        shouldThrow<ShopReviewAlreadyRepliedException> { replyCommandHandler.createReply(request) }
    }

    @Test
    @DisplayName("[deleteReply] 1. shopId가 비어서 들어오는 경우")
    fun deleteReply1(): Unit = runBlocking {
        // given
        val shopId = ""
        val reviewId = "review-id"
        val replyId = "reply-id"
        val memberId = "doccimann"

        val request = MockServerRequest.builder()
            .queryParam("shop-id", shopId)
            .queryParam("review-id", reviewId)
            .queryParam("reply-id", replyId)
            .queryParam("member-id", memberId)
            .build()

        // then
        shouldThrow<RequestFieldException> { replyCommandHandler.deleteReply(request) }
    }

    @Test
    @DisplayName("[deleteReply] 2. reviewId가 비어서 들어오는 경우")
    fun deleteReply2(): Unit = runBlocking {
        // given
        val shopId = "shop-id"
        val reviewId = ""
        val replyId = "reply-id"
        val memberId = "doccimann"

        val request = MockServerRequest.builder()
            .queryParam("shop-id", shopId)
            .queryParam("review-id", reviewId)
            .queryParam("reply-id", replyId)
            .queryParam("member-id", memberId)
            .build()

        // then
        shouldThrow<RequestFieldException> { replyCommandHandler.deleteReply(request) }
    }

    @Test
    @DisplayName("[deleteReply] 3. replyId가 비어서 들어오는 경우")
    fun deleteReply3(): Unit = runBlocking {
        // given
        val shopId = "shop-id"
        val reviewId = "review-id"
        val replyId = ""
        val memberId = "doccimann"

        val request = MockServerRequest.builder()
            .queryParam("shop-id", shopId)
            .queryParam("review-id", reviewId)
            .queryParam("reply-id", replyId)
            .queryParam("member-id", memberId)
            .build()

        // then
        shouldThrow<RequestFieldException> { replyCommandHandler.deleteReply(request) }
    }

    @Test
    @DisplayName("[deleteReply] 4. memberId가 비어서 들어오는 경우")
    fun deleteReply4(): Unit = runBlocking {
        // given
        val shopId = "shop-id"
        val reviewId = "review-id"
        val replyId = "reply-id"
        val memberId = ""

        val request = MockServerRequest.builder()
            .queryParam("shop-id", shopId)
            .queryParam("review-id", reviewId)
            .queryParam("reply-id", replyId)
            .queryParam("member-id", memberId)
            .build()

        // then
        shouldThrow<RequestFieldException> { replyCommandHandler.deleteReply(request) }
    }

    @Test
    @DisplayName("[deleteReply] 5. member가 해당 shop의 주인이 아닌 경우")
    fun deleteReply5(): Unit = runBlocking {
        // given
        val shopId = "shop-id"
        val reviewId = "review-id"
        val replyId = "reply-id"
        val memberId = "doccimann"

        val request = MockServerRequest.builder()
            .queryParam("shop-id", shopId)
            .queryParam("review-id", reviewId)
            .queryParam("reply-id", replyId)
            .queryParam("member-id", memberId)
            .build()

        // when
        coEvery { shopGrpcClient.isOwnerOfShop(memberId, shopId) } returns
                CheckIsOwnerOfShopResponse.newBuilder()
                    .setResult(false)
                    .build()

        // then
        shouldThrow<MemberNotOwnerException> { replyCommandHandler.deleteReply(request) }
    }

    @Test
    @DisplayName("[deleteReply] 6. member가 shop의 주인은 맞으나, review가 존재하지 않는 경우")
    fun deleteReply6(): Unit = runBlocking {
        // given
        val shopId = "shop-id"
        val reviewId = "review-id"
        val replyId = "reply-id"
        val memberId = "doccimann"

        val request = MockServerRequest.builder()
            .queryParam("shop-id", shopId)
            .queryParam("review-id", reviewId)
            .queryParam("reply-id", replyId)
            .queryParam("member-id", memberId)
            .build()

        // when
        coEvery { shopGrpcClient.isOwnerOfShop(memberId, shopId) } returns
                CheckIsOwnerOfShopResponse.newBuilder()
                    .setResult(true)
                    .build()

        coEvery { shopReviewGrpcClient.isExistShopReview(reviewId) } returns
                CheckExistShopReviewResponse.newBuilder()
                    .setResult(false)
                    .build()

        // then
        shouldThrow<ShopReviewNotFoundException> { replyCommandHandler.deleteReply(request) }
    }

    @Test
    @DisplayName("[deleteReply] 7. reply가 존재하지 않는 경우")
    fun deleteReply7(): Unit = runBlocking {
        // given
        val shopId = "shop-id"
        val reviewId = "review-id"
        val replyId = "reply-id"
        val memberId = "doccimann"

        val request = MockServerRequest.builder()
            .queryParam("shop-id", shopId)
            .queryParam("review-id", reviewId)
            .queryParam("reply-id", replyId)
            .queryParam("member-id", memberId)
            .build()

        // when
        coEvery { shopGrpcClient.isOwnerOfShop(memberId, shopId) } returns
                CheckIsOwnerOfShopResponse.newBuilder()
                    .setResult(true)
                    .build()

        coEvery { shopReviewGrpcClient.isExistShopReview(reviewId) } returns
                CheckExistShopReviewResponse.newBuilder()
                    .setResult(true)
                    .build()

        coEvery { replyGrpcClient.isExistReply(reviewId, replyId) } returns
                CheckIsExistReplyResponse.newBuilder()
                    .setResult(false)
                    .build()

        // then
        shouldThrow<ReplyNotFoundException> { replyCommandHandler.deleteReply(request) }
    }

    @Test
    @DisplayName("[deleteReply] 8. 모든 조건을 만족하여 validator를 통과하는 경우")
    fun deleteReply8(): Unit = runBlocking {
        // given
        val shopId = "shop-id"
        val reviewId = "review-id"
        val replyId = "reply-id"
        val memberId = "doccimann"

        val request = MockServerRequest.builder()
            .queryParam("shop-id", shopId)
            .queryParam("review-id", reviewId)
            .queryParam("reply-id", replyId)
            .queryParam("member-id", memberId)
            .build()

        val deleteRequest = ReplyCommand.DeleteRequest.of(shopId, reviewId, replyId, memberId)

        // when
        coEvery { shopGrpcClient.isOwnerOfShop(memberId, shopId) } returns
                CheckIsOwnerOfShopResponse.newBuilder()
                    .setResult(true)
                    .build()

        coEvery { shopReviewGrpcClient.isExistShopReview(reviewId) } returns
                CheckExistShopReviewResponse.newBuilder()
                    .setResult(true)
                    .build()

        coEvery { replyGrpcClient.isExistReply(reviewId, replyId) } returns
                CheckIsExistReplyResponse.newBuilder()
                    .setResult(true)
                    .build()

        // then
        val replyResponse = replyCommandHandler.deleteReply(request)

        coVerify(exactly = 1) { replyCommandHandler.deleteReply(request) }
        coVerify(exactly = 1) { replyValidator.validateDeletable(deleteRequest) }
        coVerify(exactly = 1) { shopGrpcClient.isOwnerOfShop(memberId, shopId) }
        coVerify(exactly = 1) { shopReviewGrpcClient.isExistShopReview(reviewId) }
        coVerify(exactly = 1) { replyGrpcClient.isExistReply(reviewId, replyId) }
        assertEquals(replyResponse.statusCode(), HttpStatus.OK)
    }

    private fun generateCreateRequest() = ReplyCommand.CreateRequest(
        memberId = "doccimann",
        shopId = "shop1",
        reviewId = "review1",
        content = "만족스러우셨다니 다행입니다!"
    )

    private inline fun createRequest(block: () -> Any): MockServerRequest {
        return MockServerRequest.builder()
            .body(block())
    }

    private fun getContent(length: Int): String {
        val sb = java.lang.StringBuilder()

        repeat(length) {
            sb.append('s')
        }

        return sb.toString()
    }
}