package team.bakkas.dao

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["team.bakkas"])
class InfrastructureApplication

fun main(args: Array<String>) {
    runApplication<InfrastructureApplication>(*args)
}
