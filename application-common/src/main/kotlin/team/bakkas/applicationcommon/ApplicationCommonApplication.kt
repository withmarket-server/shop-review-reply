package team.bakkas.applicationcommon

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["team.bakkas"])
class ApplicationCommonApplication

fun main(args: Array<String>) {
    runApplication<ApplicationCommonApplication>(*args)
}
