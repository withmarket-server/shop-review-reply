plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    api(project(":domain-rds"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa:2.6.6")
}

tasks.register("prepareKotlinBuildScriptModel"){}