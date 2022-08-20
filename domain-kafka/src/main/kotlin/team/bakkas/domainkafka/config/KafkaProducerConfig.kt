package team.bakkas.domainkafka.config

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer
import team.bakkas.clientcommand.dto.ShopCommand
import team.bakkas.clientquery.dto.ShopQuery
import team.bakkas.clientquery.dto.ShopReviewQuery
import team.bakkas.domaindynamo.entity.Shop
import team.bakkas.domaindynamo.entity.ShopReview

@Configuration
class KafkaProducerConfig(
    @Value("\${spring.kafka.producer.bootstrap-servers}") private val bootstrapServers: String,
    @Value("\${spring.kafka.producer.acks}") private val producerAcks: String
) {

    // Shop에 대한 Kafka template
    @Bean
    fun shopKafkaTemplate(): KafkaTemplate<String, Shop> {
        return KafkaTemplate(shopProducerFactory())
    }

    // Shop의 개수를 dynamo와 redis 간에 정합을 맞추는데 사용하는 template
    @Bean
    fun shopCountKafkaTemplate(): KafkaTemplate<String, ShopQuery.ShopCountDto> {
        return KafkaTemplate(shopCountProducerFactory())
    }

    // ShopRwview에 대한 Kafka Template
    @Bean
    fun shopReviewKafkaTemplate(): KafkaTemplate<String, ShopReview> {
        return KafkaTemplate(shopReviewProducerFactory())
    }

    // Shop에 대해서 review 생성 이벤트를 처리하는데 사용하는 template
    @Bean
    fun reviewGeneratedEventKafkaTemplate(): KafkaTemplate<String, ShopCommand.ReviewCountEventDto> {
        return KafkaTemplate(reviewGeneratedEventProducerFactory())
    }

    // shop에 대한 review의 목록이 조회되었을 때 이벤트를 발행하기 위한 template
    @Bean
    fun reviewCountValidateKakfaTemplate(): KafkaTemplate<String, ShopReviewQuery.ShopReviewCountDto> {
        return KafkaTemplate(reviewCountValidateEventProducerFactory())
    }

    // Shop에 대한 Kafka Template를 사용하기 위한 Producer Factory
    private fun shopProducerFactory(): ProducerFactory<String, Shop> =
        DefaultKafkaProducerFactory(producerConfig())

    // Shop의 개수를 정합 맞추는데 사용하는 producer factory
    private fun shopCountProducerFactory(): ProducerFactory<String, ShopQuery.ShopCountDto> =
        DefaultKafkaProducerFactory(producerConfig())

    // ShopReview에 대한 Template를 사용하기 위한 Producer Factory
    private fun shopReviewProducerFactory(): ProducerFactory<String, ShopReview> =
        DefaultKafkaProducerFactory(producerConfig())

    // review가 작성됐을 때의 Event Template를 사용하기 위한 Producer Factory
    private fun reviewGeneratedEventProducerFactory(): ProducerFactory<String, ShopCommand.ReviewCountEventDto> =
        DefaultKafkaProducerFactory(producerConfig())

    // review 목록이 조회되었을 때 발행되는 이벤트를 처리하기 위한 Producer factory
    private fun reviewCountValidateEventProducerFactory(): ProducerFactory<String, ShopReviewQuery.ShopReviewCountDto> =
        DefaultKafkaProducerFactory(producerConfig())

    // producer config를 반환해주는 메소드
    private fun producerConfig(): HashMap<String, Any> {
        val configProps: HashMap<String, Any> = hashMapOf()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        configProps[ProducerConfig.ACKS_CONFIG] = producerAcks
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java

        return configProps
    }
}