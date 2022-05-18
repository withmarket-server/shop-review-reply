plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.register("prepareKotlinBuildScriptModel"){}