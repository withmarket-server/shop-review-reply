package team.bakkas.domainredis.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.CacheKeyPrefix
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import team.bakkas.domainredis.cacheInfo.CacheExpirationTimeInfo
import team.bakkas.domainredis.cacheInfo.CacheKeyInfo
import java.time.Duration
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

/** Redis를 사용하기 위한 configuration class
 * @since 22/06/09
 * @author Brian
 * @param redisHost host of redis
 * @param redisPort port of redis
 * @see <a href="https://github.com/kalekarnn/redis-dynamodb-springboot">Redis example (redis-dynamodb-springboot)</a>
 */
@Configuration
@EnableRedisRepositories
@EnableCaching
class RedisRepositoryConfig(
    @Value("\${spring.redis.port}")
    private val redisPort: Int,
    @Value("\${spring.redis.host}")
    private val redisHost: String
) {
    @Bean
    fun redisConnectionFactory() = LettuceConnectionFactory(redisHost, redisPort)

    @Bean
    fun redisTemplate(): RedisTemplate<String, JvmType.Object> {
        val redisTemplate = RedisTemplate<String, JvmType.Object>()

        with(redisTemplate) {
            this.setConnectionFactory(redisConnectionFactory())
            this.keySerializer = StringRedisSerializer()
            this.hashKeySerializer = StringRedisSerializer()
            this.hashKeySerializer = JdkSerializationRedisSerializer()
            this.valueSerializer = JdkSerializationRedisSerializer()
            this.setEnableTransactionSupport(true)
            this.afterPropertiesSet()
        }

        return redisTemplate
    }

    /* Caching 에 대한 전략을 설정하는 bean method
     * disableCachingNullValues() : null value 의 경우에는 캐싱을 하지않는다.
     * entryTtl : 캐시의 기본 유효시간을 설정
     * computePrefixWith(CacheKeyPrefix.simple()) : value 와 key 로 만들어지는 Key 값을 ::로 구분
     * serializeKeysWith : 캐시 Key를 직렬화-역직렬화 하는데 사용하는 Pair를 지정 -> String
     * serializaValueWith : 캐시 Value를 직렬화-역직렬화 하는데 사용하는 Pair를 지정 -> JdkSerializationRedisSerializer 로 설정
     *
     * cacheConfiguration : key-value 구조로 캐시 키별로 유효시간을 따로 설정하는 map 객체
     */
    @Bean
    fun redisCacheManager(): CacheManager {
        val cacheConfiguration = getCacheConfiguration()
        val configuration = RedisCacheConfiguration.defaultCacheConfig()
            .disableCachingNullValues()
            .entryTtl(Duration.ofSeconds(CacheExpirationTimeInfo.DEFAULT_EXPIRATION_SEC))
            .computePrefixWith(CacheKeyPrefix.simple())
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair
                    .fromSerializer(StringRedisSerializer())
            )
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair
                    .fromSerializer(JdkSerializationRedisSerializer())
            )

        return RedisCacheManager.RedisCacheManagerBuilder
            .fromConnectionFactory(redisConnectionFactory())
            .cacheDefaults(configuration)
            .withInitialCacheConfigurations(cacheConfiguration)
            .build()
    }

    // cacheConfiguration을 반환하는 메소드
    private fun getCacheConfiguration(): Map<String, RedisCacheConfiguration> {
        val cacheConfiguration = mutableMapOf<String, RedisCacheConfiguration>()

        /*
         * zone 에 대해서 기본 유효시간을 설정
         * 캐싱 데이터의 키는 value::key 형태로 저장되므로, zone 에 대해서는 zone::{id} 형태로 저장된다.
         * 추가적인 key strategy 를 설정하고싶다면 cacheConfiguration 에 key, ttl 쌍을 추가적으로 설정
         */
        val keyList = listOf(CacheKeyInfo.ZONE, CacheKeyInfo.SHOP_LIST, CacheKeyInfo.SHOP_REVIEW_LIST)
        val expirationTimeList = listOf(
            CacheExpirationTimeInfo.ZONE_EXPIRATION_SEC, CacheExpirationTimeInfo.SHOP_LIST_EXPIRATION_SEC,
            CacheExpirationTimeInfo.SHOP_REVIEW_LIST_EXPIRATION_SEC
        )

        val keyInfoPairList = keyList.zip(expirationTimeList) // pair의 목록

        keyInfoPairList.forEach { pair ->
            cacheConfiguration[pair.first] = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(pair.second))
        }

        return cacheConfiguration
    }
}