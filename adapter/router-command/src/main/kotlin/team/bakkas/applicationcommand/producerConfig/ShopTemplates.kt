package team.bakkas.applicationcommand.producerConfig

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.stereotype.Component
import team.bakkas.clientcommand.shop.ShopCommand
import team.bakkas.dynamo.shop.Shop

/**
 * ShopTemplates
 * shop에 대한 event를 발행하는데 사용되는 kafkaTemplate들을 정의하는 spring component
 * @param bootstrapServers
 * @param producerAcks
 * @since 2022/11/27
 */
@Component
class ShopTemplates(
    @Value("\${spring.kafka.producer.bootstrap-servers}") private val bootstrapServers: String,
    @Value("\${spring.kafka.producer.acks}") private val producerAcks: String
) {

    @Bean
    fun shopKafkaTemplate(): KafkaTemplate<String, Shop> {
        return KafkaTemplate(shopProducerFactory())
    }

    @Bean
    fun shopUpdatedEventKafkaTemplate(): KafkaTemplate<String, ShopCommand.UpdateRequest> {
        return KafkaTemplate(shopUpdatedEventProducerFactory())
    }

    @Bean
    fun shopDeletedEventKafkaTemplate(): KafkaTemplate<String, ShopCommand.DeletedEvent> {
        return KafkaTemplate(shopDeletedEventProducerFactory())
    }

    private fun shopProducerFactory(): ProducerFactory<String, Shop> =
        DefaultKafkaProducerFactory(producerConfig())

    private fun shopUpdatedEventProducerFactory(): ProducerFactory<String, ShopCommand.UpdateRequest> =
        DefaultKafkaProducerFactory(producerConfig())

    private fun shopDeletedEventProducerFactory(): ProducerFactory<String, ShopCommand.DeletedEvent> =
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