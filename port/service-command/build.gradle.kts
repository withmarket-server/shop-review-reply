dependencies {
    // Connect the dependencies among the projects of this project
    api(project(":port:client-command"))
    api(project(":port:repository"))
    api(project(":domain:dynamo"))
    api(project(":adapter:dao"))

    // Spring Kafka
    implementation("org.springframework.kafka:spring-kafka")
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