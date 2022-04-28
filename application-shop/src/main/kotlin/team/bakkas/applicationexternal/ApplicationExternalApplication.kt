package team.bakkas.applicationexternal

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ApplicationExternalApplication

fun main(args: Array<String>) {
    runApplication<ApplicationExternalApplication>(*args)
}
