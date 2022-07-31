package team.bakkas.domainshopcommand

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["team.bakkas"])
class DomainShopcommandApplication

fun main(args: Array<String>) {
    runApplication<DomainShopcommandApplication>(*args)
}
