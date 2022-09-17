package team.bakkas.infrastructure.config.redis

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory

@Configuration
@Profile("localcache")
class LocalRedisConfig(
    @Value("\${spring.redis.host}")
    val host: String,
    @Value("\${spring.redis.port}")
    val port: Int
) : RedisConfig {

    @Primary
    @Bean
    override fun connectionFactory(): ReactiveRedisConnectionFactory? {
        return LettuceConnectionFactory(host, port)
    }

}