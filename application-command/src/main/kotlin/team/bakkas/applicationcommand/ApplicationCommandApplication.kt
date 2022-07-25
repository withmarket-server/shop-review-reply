package team.bakkas.applicationcommand

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["team.bakkas"])
class ApplicationCommandApplication

fun main(args: Array<String>) {
    runApplication<ApplicationCommandApplication>(*args)
}
