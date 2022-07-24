package team.bakkas.clientcommand

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ClientCommandApplication

fun main(args: Array<String>) {
    runApplication<ClientCommandApplication>(*args)
}
