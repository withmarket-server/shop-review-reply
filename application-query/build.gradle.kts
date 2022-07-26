plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

extra["springCloudVersion"] = "2021.0.2"

dependencies {
    // 모듈 간 의존관계 주입
    api(project(":domain-shopquery"))

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation("org.springframework.cloud:spring-cloud-starter-config")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.3")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.3")

    // mockk
    testImplementation("io.mockk:mockk:1.12.0")
    testImplementation("io.kotest:kotest-runner-junit5:5.3.2") // for kotest framework
    testImplementation("io.kotest:kotest-assertions-core:5.3.2") // for kotest core jvm assertions
    testImplementation("io.kotest:kotest-property:5.3.2") // for kotest property test
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.register("prepareKotlinBuildScriptModel"){}