package team.bakkas.domaindynamo

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ConfigController {

    @Value("\${aws.dynamodb.credentials.access-key}")
    private lateinit var accessKey: String

    @GetMapping("/access-key")
    fun getKey(): String = accessKey
}