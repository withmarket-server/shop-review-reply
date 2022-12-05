package team.bakkas.applicationkafka.eventHandlers

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import team.bakkas.applicationkafka.extensions.toEntity
import team.bakkas.clientcommand.reply.ReplyCommand
import team.bakkas.eventinterface.kafka.KafkaConsumerGroups
import team.bakkas.eventinterface.kafka.KafkaTopics
import team.bakkas.servicecommand.service.ifs.ReplyCommandService
import team.bakkas.servicecommand.service.ifs.ShopReviewCommandService

/**
 * ReplyCommandHandler
 * reply에 대해서 발행된 이벤트들을 컨슘해서 처리하는 event handler class
 * @param shopReviewCommandService
 * @param replyCommandService
 * @since 2022/12/05
 */
@Component
class ReplyEventHandler(
    private val shopReviewCommandService: ShopReviewCommandService,
    private val replyCommandService: ReplyCommandService
) {

    @KafkaListener(
        topics = [KafkaTopics.replyCreateTopic],
        groupId = KafkaConsumerGroups.replyGroup
    )
    fun createReply(createdEvent: ReplyCommand.CreatedEvent) {
        val reply = createdEvent.toEntity()

        replyCommandService.createReply(reply) // 답글을 record system에 기록하고
            .doOnSuccess { shopReviewCommandService.applyReplyCreated(it.reviewId).subscribe() } // 리뷰가 작성된 것을 반영한다
            .subscribe()
    }
}