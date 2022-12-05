package team.bakkas.applicationcommand.handler

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.common.runBlocking
import io.mockk.coEvery
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.spyk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.mock.web.reactive.function.server.MockServerRequest
import reactor.core.publisher.Mono
import team.bakkas.applicationcommand.grpc.ifs.ShopGrpcClient
import team.bakkas.applicationcommand.grpc.ifs.ShopReviewGrpcClient
import team.bakkas.applicationcommand.validator.ReplyValidatorImpl
import team.bakkas.clientcommand.reply.ReplyCommand
import team.bakkas.common.exceptions.RequestBodyLostException
import team.bakkas.common.exceptions.RequestFieldException
import team.bakkas.common.exceptions.shop.MemberNotOwnerException
import team.bakkas.common.exceptions.shopReview.ShopReviewAlreadyRepliedException
import team.bakkas.common.exceptions.shopReview.ShopReviewNotFoundException
import team.bakkas.eventinterface.eventProducer.ReplyEventProducer
import team.bakkas.grpcIfs.v1.shop.CheckIsOwnerOfShopResponse
import team.bakkas.grpcIfs.v1.shopReview.CheckExistShopReviewResponse
import team.bakkas.grpcIfs.v1.shopReview.CheckIsRepliedReviewResponse
import team.bakkas.servicecommand.validator.ReplyValidator

@ExtendWith(MockKExtension::class)
internal class ReplyCommandHandlerTest {
    private lateinit var replyCommandHandler: ReplyCommandHandler

    private lateinit var replyEventProducer: ReplyEventProducer

    private lateinit var replyValidator: ReplyValidator

    private lateinit var shopReviewGrpcClient: ShopReviewGrpcClient

    private lateinit var shopGrpcClient: ShopGrpcClient

    @BeforeEach
    fun setUp() {
        shopReviewGrpcClient = mockk(relaxed = true)
        shopGrpcClient = mockk(relaxed = true)
        replyValidator = spyk(ReplyValidatorImpl(shopGrpcClient, shopReviewGrpcClient))
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