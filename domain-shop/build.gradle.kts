plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    api(project(":domain-rds"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa:2.6.6")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.register("prepareKotlinBuildScriptModel"){}