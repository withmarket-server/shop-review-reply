package team.bakkas.dao.repository.redis

import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import team.bakkas.common.utils.RedisUtils
import team.bakkas.dynamo.reply.Reply
import team.bakkas.repository.ifs.redis.ReplyRedisRepository
import java.time.Duration

/**
 * @author Doyeop Kim
 * @since 2022/12/05
 */
@Repository
class ReplyRedisRepositoryImpl(
    private val replyReactiveRedisTemplate: ReactiveRedisTemplate<String, Reply>
) : ReplyRedisRepository {

    override fun cacheReply(reply: Reply): Mono<Reply> = with(reply) {
        val replyKey = RedisUtils.generateReplyRedisKey(replyId)

        replyReactiveRedisTemplate.opsForValue().set(replyKey, this, Duration.ofDays(RedisUtils.DAYS_TO_LIVE))
            .thenReturn(this)
    }
}