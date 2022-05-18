plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

extra["springCloudVersion"] = "2021.0.2"

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation("org.springframework.cloud:spring-cloud-starter-config")

    implementation("software.amazon.awssdk:dynamodb-enhanced:2.17.101")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.register("prepareKotlinBuildScriptModel"){}