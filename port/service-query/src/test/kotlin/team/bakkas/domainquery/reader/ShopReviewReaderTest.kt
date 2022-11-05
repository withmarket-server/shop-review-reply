package team.bakkas.domainquery.reader

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.CoroutinesUtils
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.util.StopWatch
import reactor.core.publisher.Mono
import team.bakkas.dynamo.shopReview.ShopReview
import team.bakkas.repository.ifs.dynamo.ShopReviewDynamoRepository
import java.time.Duration

@SpringBootTest
internal class ShopReviewReaderTest @Autowired constructor(
    val shopReviewDynamoRepository: ShopReviewDynamoRepository,
    val shopReviewReader: ShopReviewReaderImpl,
    val shopReviewReactiveRedisTemplate: ReactiveRedisTemplate<String, ShopReview>
) {


}