dependencies {
    // Connect dependencies among the modules of this project
    api(project(":domain:dynamo"))
    api(project(":port:repository"))
    api(project(":adapter:dao"))

    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
}

// reader에 대해서는 테스트하지 않는다
tasks.withType<Test> {
    exclude("**/reader/**")
}

// root folder의 gradle project는 빌드하지 않는다
tasks {
    named<Jar>("jar") {
        enabled = true
    }

    named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
        enabled = false
    }
}