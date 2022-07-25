package team.bakkas.clientkafka

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ClientKafkaApplication

fun main(args: Array<String>) {
    runApplication<ClientKafkaApplication>(*args)
}
