package team.bakkas.domaindynamo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.core.env.AbstractEnvironment

@SpringBootApplication
class DomainDynamoApplication

fun main(args: Array<String>) {
    System.setProperty("spring.profiles.active", "dev")

    runApplication<DomainDynamoApplication>(*args)
}
