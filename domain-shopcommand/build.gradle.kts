import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

extra["springCloudVersion"] = "2021.0.2"

dependencies {
    api(project(":domain-dynamo"))
    api(project(":client-command"))

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    implementation("org.springframework.cloud:spring-cloud-starter-config")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.3")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.register("prepareKotlinBuildScriptModel") {}