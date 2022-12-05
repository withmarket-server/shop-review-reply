package team.bakkas.applicationcommand.handler

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import team.bakkas.clientcommand.reply.ReplyCommand
import team.bakkas.common.ResultFactory
import team.bakkas.common.exceptions.RequestBodyLostException
import team.bakkas.eventinterface.eventProducer.ReplyEventProducer
import team.bakkas.servicecommand.validator.ReplyValidator

/**
 * ReplyCommandHandler
 * Reply에 대한 request들을 validate하고 합당한 request이면 대응하는 이벤트를 발행하는 class
 * @param replyValidator reply에 대한 command request를 검증하는 validator class
 * @param replyEventProducer reply에 대해 검증된 command request에 대해서 이벤트를 발행하는 객체
 */
@Component
class ReplyCommandHandler(
    private val replyValidator: ReplyValidator,
    private val replyEventProducer: ReplyEventProducer
) {

    suspend fun createReply(serverRequest: ServerRequest): ServerResponse = coroutineScope {
        val createRequest = serverRequest.bodyToMono(ReplyCommand.CreateRequest::class.java)
            .awaitSingleOrNull()

        checkNotNull(createRequest) {
            throw RequestBodyLostException("Body is lost!!")
        }

        replyValidator.validateCreatable(createRequest)

        val createdEvent = createRequest.transformToEvent()

        replyEventProducer.propagateReplyCreated(createdEvent)

        return@coroutineScope ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(ResultFactory.getSuccessResult())
    }
}