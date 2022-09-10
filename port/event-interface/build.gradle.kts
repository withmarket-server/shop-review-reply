dependencies {
    // Connect the dependencies among the modules of this project
    api(project(":port:client-query"))
    api(project(":port:client-command"))
    api(project(":domain:dynamo"))

    // spring-kafka
    implementation("org.springframework.kafka:spring-kafka")
}