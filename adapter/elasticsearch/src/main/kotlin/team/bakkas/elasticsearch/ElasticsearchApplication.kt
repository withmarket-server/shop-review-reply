package team.bakkas.elasticsearch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["team.bakkas"])
class ElasticsearchApplication

fun main(args: Array<String>) {
    runApplication<ElasticsearchApplication>(*args)
}
