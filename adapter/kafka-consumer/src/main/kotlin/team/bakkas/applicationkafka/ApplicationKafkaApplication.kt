package team.bakkas.applicationkafka

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["team.bakkas"])
class ApplicationKafkaApplication

fun main(args: Array<String>) {
    runApplication<ApplicationKafkaApplication>(*args)
}
