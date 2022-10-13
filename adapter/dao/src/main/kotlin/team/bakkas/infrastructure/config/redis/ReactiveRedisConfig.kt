package team.bakkas.infrastructure.config.redis

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import team.bakkas.dynamo.shop.Shop
import team.bakkas.dynamo.shopReview.ShopReview

@Configuration
class ReactiveRedisConfig {

    /**
     * ReactiveRedisTemplate를 생성하기 위한 공통 로직을 선언한 메소드
     * @param factory Lettuce를 이용한 Non-blocking connection factory
     * @param clazz ReactiveRedisTemplate에서 사용할 클래스
     */
    private fun <T> commonReactiveRedisTemplate(
        factory: ReactiveRedisConnectionFactory?,
        clazz: Class<T>
    ): ReactiveRedisTemplate<String, T> {
        val keySerializer = StringRedisSerializer()
        val redisSerializer = Jackson2JsonRedisSerializer(clazz)
            .apply {
                setObjectMapper(
                    jacksonObjectMapper()
                        .registerModule(JavaTimeModule())
                )
            }

        val serializationContext = RedisSerializationContext
            .newSerializationContext<String, T>()
            .key(keySerializer)
            .hashKey(keySerializer)
            .value(redisSerializer)
            .hashValue(redisSerializer)
            .build()

        return ReactiveRedisTemplate(factory!!, serializationContext)
    }

    // Shop에 대한 reactive redis template
    @Bean
    fun shopReactiveRedisTemplate(
        factory: ReactiveRedisConnectionFactory,
    ): ReactiveRedisTemplate<String, Shop> = commonReactiveRedisTemplate(factory, Shop::class.java)

    // ShopReview에 대한 reactive redis template
    @Bean
    fun shopReviewReactiveRedisTemplate(
        factory: ReactiveRedisConnectionFactory
    ): ReactiveRedisTemplate<String, ShopReview> = commonReactiveRedisTemplate(factory, ShopReview::class.java)
}