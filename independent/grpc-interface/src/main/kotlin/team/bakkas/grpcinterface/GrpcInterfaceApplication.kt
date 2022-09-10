package team.bakkas.grpcinterface

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GrpcInterfaceApplication

fun main(args: Array<String>) {
    runApplication<GrpcInterfaceApplication>(*args)
}
