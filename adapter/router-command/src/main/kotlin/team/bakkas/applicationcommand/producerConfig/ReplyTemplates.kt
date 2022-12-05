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
import team.bakkas.clientcommand.reply.ReplyCommand

/**
 * ReplyTemplates
 * Reply 관련 event를 발행하는데 사용되는 Kafka templates
 * @param bootstrapServers
 * @param producerAcks
 */
@Component
class ReplyTemplates(
    @Value("\${spring.kafka.producer.bootstrap-servers}") private val bootstrapServers: String,
    @Value("\${spring.kafka.producer.acks}") private val producerAcks: String
) {

    @Bean
    fun replyCreatedEventKafkaTemplate(): KafkaTemplate<String, ReplyCommand.CreatedEvent> {
        return KafkaTemplate(replyCreatedEventProducerFactory())
    }

    private fun replyCreatedEventProducerFactory(): ProducerFactory<String, ReplyCommand.CreatedEvent> {
        return DefaultKafkaProducerFactory(producerConfig())
    }

    private fun producerConfig(): HashMap<String, Any> {
        val configProps: HashMap<String, Any> = hashMapOf()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        configProps[ProducerConfig.ACKS_CONFIG] = producerAcks
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java

        return configProps
    }
}