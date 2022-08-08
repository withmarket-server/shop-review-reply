package team.bakkas.applicationcommand.config

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

    // ShopRwview에 대한 Kafka Template
    @Bean
    fun shopReviewKafkaTemplate(): KafkaTemplate<String, ShopReview> {
        return KafkaTemplate(shopReviewProducerFactory())
    }

    // Shop에 대해서 review 생성 이벤트를 처리하는데 사용하는 template
    @Bean
    fun reviewCountKafkaTemplate(): KafkaTemplate<String, ShopCommand.ReviewCountEventDto> {
        return KafkaTemplate(reviewCountEventProducerFactory())
    }

    // Shop에 대한 Kafka Template를 사용하기 위한 Producer Factory
    private fun shopProducerFactory(): ProducerFactory<String, Shop> {
        val configProps = producerConfig()
        return DefaultKafkaProducerFactory(configProps)
    }

    // ShopReview에 대한 Template를 사용하기 위한 Producer Factory
    private fun shopReviewProducerFactory(): ProducerFactory<String, ShopReview> {
        val configProps = producerConfig()
        return DefaultKafkaProducerFactory(configProps)
    }

    private fun reviewCountEventProducerFactory(): ProducerFactory<String, ShopCommand.ReviewCountEventDto> {
        val configProps = producerConfig()
        return DefaultKafkaProducerFactory(configProps)
    }

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