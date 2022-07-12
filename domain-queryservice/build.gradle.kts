plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

extra["springCloudVersion"] = "2021.0.2"

dependencies {
    // 모듈 간 의존관계 주입
    api(project(":client-mobilequery"))
    api(project(":domain-dynamo"))
    api(project(":common"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.3")

    // mockk
    testImplementation("io.mockk:mockk:1.12.0")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.register("prepareKotlinBuildScriptModel"){}