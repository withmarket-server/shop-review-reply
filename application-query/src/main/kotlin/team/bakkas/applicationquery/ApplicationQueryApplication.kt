package team.bakkas.applicationquery

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["team.bakkas"])
class ApplicationQueryApplication

fun main(args: Array<String>) {
    runApplication<ApplicationQueryApplication>(*args)
}
