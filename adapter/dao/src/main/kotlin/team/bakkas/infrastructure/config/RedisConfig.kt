package team.bakkas.infrastructure.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory

@Configuration
class RedisConfig(
    @Value("\${spring.redis.host}")
    val host: String,
    @Value("\${spring.redis.port}")
    val port: Int
) {

    @Primary
    @Bean
    fun connectionFactory(): ReactiveRedisConnectionFactory? {
        return LettuceConnectionFactory(host, port)
    }

}