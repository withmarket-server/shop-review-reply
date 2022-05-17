plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    api(project(":domain-shop"))
    implementation("org.springframework.boot:spring-boot-starter-web:2.6.6")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.register("prepareKotlinBuildScriptModel"){}