import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    // spring kafka
    implementation("org.springframework.kafka:spring-kafka:2.8.6")
}

tasks.register("prepareKotlinBuildScriptModel") {}