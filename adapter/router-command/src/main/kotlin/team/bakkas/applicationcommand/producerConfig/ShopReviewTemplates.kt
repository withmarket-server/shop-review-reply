package team.bakkas.applicationcommand.producerConfig

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer
import team.bakkas.clientcommand.shopReview.ShopReviewCommand
import team.bakkas.dynamo.shopReview.ShopReview

/**
 * ShopReviewTemplates
 * shopReview 관련 event를 발행하는데 사용되는 kafka templates
 * @param bootstrapServers
 * @param producerAcks
 * @since 2022/11/27
 */
class ShopReviewTemplates(
    @Value("\${spring.kafka.producer.bootstrap-servers}") private val bootstrapServers: String,
    @Value("\${spring.kafka.producer.acks}") private val producerAcks: String
) {

    @Bean
    fun shopReviewKafkaTemplate(): KafkaTemplate<String, ShopReview> {
        return KafkaTemplate(shopReviewProducerFactory())
    }

    @Bean
    fun reviewDeletedEventKafkaTemplate(): KafkaTemplate<String, ShopReviewCommand.DeletedEvent> {
        return KafkaTemplate(reviewDeletedEventProducerFactory())
    }

    private fun shopReviewProducerFactory(): ProducerFactory<String, ShopReview> =
        DefaultKafkaProducerFactory(producerConfig())

    private fun reviewDeletedEventProducerFactory(): ProducerFactory<String, ShopReviewCommand.DeletedEvent> =
        DefaultKafkaProducerFactory(producerConfig())

    private fun producerConfig(): HashMap<String, Any> {
        val configProps: HashMap<String, Any> = hashMapOf()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        configProps[ProducerConfig.ACKS_CONFIG] = producerAcks
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java

        return configProps
    }
}