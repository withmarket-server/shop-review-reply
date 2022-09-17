package team.bakkas.infrastructure.config.redis

import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory

interface RedisConfig {

    fun connectionFactory(): ReactiveRedisConnectionFactory?
}