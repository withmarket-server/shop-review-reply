package team.bakkas.dao.repository.redis

import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import team.bakkas.common.utils.RedisUtils
import team.bakkas.dynamo.reply.Reply
import team.bakkas.repository.ifs.redis.ReplyRedisRepository
import java.time.Duration
import java.util.StringTokenizer

/**
 * @author Doyeop Kim
 * @since 2022/12/05
 */
@Repository
class ReplyRedisRepositoryImpl(
    private val replyReactiveRedisTemplate: ReactiveRedisTemplate<String, Reply>
) : ReplyRedisRepository {

    override fun cacheReply(reply: Reply): Mono<Reply> = with(reply) {
        val replyKey = RedisUtils.generateReplyRedisKey(replyId, reviewId)

        replyReactiveRedisTemplate.opsForValue().set(replyKey, this, Duration.ofDays(RedisUtils.DAYS_TO_LIVE))
            .thenReturn(this)
    }

    override fun findByReviewId(reviewId: String): Mono<Reply> {
        return replyReactiveRedisTemplate.scan()
            .filter { key -> isReplyOfReview(key, reviewId) } // reviewId에 대응하는 key인지 필터링하고
            .flatMap { replyReactiveRedisTemplate.opsForValue().get(it) } // 걸러진 key에 대응하는 item을 가져와서
            .singleOrEmpty() // 하나 혹은 empty를 가져온다
    }

    /**
     * isReplyOfReview(key: String, reviewId: String)
     * 첫번째 parameter로 들어온 key가 reviewId 정보를 포함하는지 여부를 반환하는 메소드
     * @param key redisKey
     * @param reviewId reply를 소유한 review의 id
     */
    private fun isReplyOfReview(key: String, reviewId: String): Boolean {
        val isReplyKey = key.split(":")[0] == "reply"
        val isReplyOfReview = key.split("+")[1] == reviewId

        return isReplyKey && isReplyOfReview
    }
}