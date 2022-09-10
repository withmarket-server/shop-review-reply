dependencies {
    // Connect the dependencies among the projects of this project
    api(project(":port:client-command"))
    api(project(":port:repository"))
    api(project(":domain:dynamo"))
    api(project(":adapter:dao"))

    // Spring Kafka
    implementation("org.springframework.kafka:spring-kafka")
}