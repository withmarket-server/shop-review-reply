package team.bakkas.domainshopcommand.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.KafkaTemplate

@SpringBootTest
internal class KafkaServiceTest @Autowired constructor(
    private val stringKafkaTemplate: KafkaTemplate<String, String>
) {

    val stringTestTopic = "withmarket.test.string"

    @Test
    @DisplayName("1. 아무 문자열 날리기")
    fun sendStringMessage1() {
        stringKafkaTemplate.send(stringTestTopic, "test1")
    }
}