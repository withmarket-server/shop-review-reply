package team.bakkas.servicecommand.service.ifs

import reactor.core.publisher.Mono
import team.bakkas.dynamo.reply.Reply

/**
 * ReplyCommandService
 * Reply에 대한 command business logic을 처리하는 service layer의 interdace
 * Clean Architecture 상의 UseCase layer에 대응한다
 * @since 2022/12/05
 */
interface ReplyCommandService {

    fun createReply(reply: Reply): Mono<Reply>
}