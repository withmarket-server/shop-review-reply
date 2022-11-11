package team.bakkas.applicationquery

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication(scanBasePackages = ["team.bakkas"])
@EnableScheduling
class ApplicationQueryApplication

fun main(args: Array<String>) {
    runApplication<ApplicationQueryApplication>(*args)
}