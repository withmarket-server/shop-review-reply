plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

extra["springCloudVersion"] = "2021.0.2"

dependencies {

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.register("prepareKotlinBuildScriptModel"){}