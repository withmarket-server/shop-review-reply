package team.bakkas.domainquery

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["team.bakkas"])
class DomainQueryserviceApplication

fun main(args: Array<String>) {
    runApplication<DomainQueryserviceApplication>(*args)
}
