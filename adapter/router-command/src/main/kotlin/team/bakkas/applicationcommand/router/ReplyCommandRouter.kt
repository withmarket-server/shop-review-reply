package team.bakkas.applicationcommand.router

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter
import team.bakkas.applicationcommand.handler.ReplyCommandHandler

/**
 * ReplyCommandHandler
 * Reply에 대한 client endpoint를 정의하는 class
 * @param replyCommandHandler reply에 대한 이벤트 발행을 책임지는 command handler
 */
@Configuration
class ReplyCommandRouter(
    private val replyCommandHandler: ReplyCommandHandler
) {
    @Bean
    fun replyCommandRoutes() = coRouter {
        "/api/v2/reply".nest {
            POST("", replyCommandHandler::createReply) // [POST] http://localhost:10101/api/v2/reply
            DELETE("", replyCommandHandler::deleteReply) // [DELETE] http://localhost:10101/api/v2/reply
        }
    }
}