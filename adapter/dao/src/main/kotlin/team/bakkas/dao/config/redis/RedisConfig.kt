package team.bakkas.dao.config.redis

import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory

interface RedisConfig {

    fun connectionFactory(): ReactiveRedisConnectionFactory?
}