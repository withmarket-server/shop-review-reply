package team.bakkas.dao.config.redis

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisClusterConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory

/**
 * ServerRedisConfig(private val clusterNodes: List<String>)
 * cluster로 구성된 redis를 사용하기 위해 선언된 Spring Configuration class
 * @param clusterNodes redis nodes consisted with cluster
 */
@Configuration
@Profile("serverCxache")
class ServerRedisConfig(
    @Value("\${spring.redis.cluster.nodes}") private val clusterNodes: List<String>
) : RedisConfig {

    @Primary
    @Bean
    override fun connectionFactory(): ReactiveRedisConnectionFactory? {
        val redisClusterConfiguration = RedisClusterConfiguration()

        clusterNodes.forEach { nodeInfo ->
            val splitedInfo = nodeInfo.split(':')
            redisClusterConfiguration.clusterNode(splitedInfo[0], Integer.parseInt(splitedInfo[1]))
        }

        return LettuceConnectionFactory(redisClusterConfiguration)
    }
}