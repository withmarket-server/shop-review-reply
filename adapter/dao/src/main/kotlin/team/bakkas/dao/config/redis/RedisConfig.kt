package team.bakkas.dao.config.redis

import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory

/**
 * RedisConfig
 * Local, Server 환경에서 반드시 선언되어야하는 애트리뷰트를 정의한 인터페이스
 */
interface RedisConfig {

    fun connectionFactory(): ReactiveRedisConnectionFactory?
}