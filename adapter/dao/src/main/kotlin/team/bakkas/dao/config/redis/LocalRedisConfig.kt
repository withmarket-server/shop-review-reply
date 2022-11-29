package team.bakkas.dao.config.redis

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory

/**
 * LocalRedisConfig(val host: String, val port: Int)
 * Local에 설치된 standalone redis를 프로젝트에 적용하기 위한 Spring Configuration class 입니다.
 * @param host redis host (localhost)
 * @param port redis port (default: 6379)
 */
@Configuration
@Profile("localCache")
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