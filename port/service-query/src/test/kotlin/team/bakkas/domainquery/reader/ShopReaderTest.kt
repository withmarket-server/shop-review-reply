package team.bakkas.domainquery.reader

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.CoroutinesUtils
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.util.StopWatch
import reactor.core.publisher.Mono
import team.bakkas.dynamo.shop.Shop
import team.bakkas.repository.ifs.dynamo.ShopDynamoRepository
import java.time.Duration

@SpringBootTest
internal class ShopReaderTest @Autowired constructor(
    val shopDynamoRepository: ShopDynamoRepository,
    val shopReader: ShopReaderImpl,
    val shopReactiveRedisTemplate: ReactiveRedisTemplate<String, Shop>
) {


}