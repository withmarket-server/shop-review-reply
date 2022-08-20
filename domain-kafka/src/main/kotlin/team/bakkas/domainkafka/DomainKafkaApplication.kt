package team.bakkas.domainkafka

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["team.bakkas"])
class DomainKafkaApplication

fun main(args: Array<String>) {
    runApplication<DomainKafkaApplication>(*args)
}
