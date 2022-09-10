rootProject.name = "yumarket"

pluginManagement {
    val kotlinVersion = "1.5.10"
    val springBootVersion = "2.6.6"
    val dependencyManagementVersion = "1.0.11.RELEASE"

    plugins {
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version dependencyManagementVersion
        id("org.jetbrains.kotlin.plugin.allopen") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.noarg") version kotlinVersion
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion
    }
}

// include projects
include(
    "adapter",
    "adapter:dao",
    "adapter:kafka-config",
    "adapter:kafka-listener",
    "adapter:router-command",
    "adapter:router-query",
    "adapter:router-common"
)

include(
    "commons",
    "commons:common"
)

include(
    "domain",
    "domain:dynamo"
)

include(
    "port",
    "port:repository",
    "port:client-command",
    "port:client-query",
    "port:service-query",
    "port:service-command",
    "port:event-interface"
)