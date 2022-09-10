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