dependencies {
    // Connect the dependencies among the modules of this project
    api(project(":port:client-query"))
    api(project(":port:service-query"))
    api(project(":port:event-interface"))
    api(project(":adapter:kafka-config")) // dependency of adapter layer for connecting to kafka

    // spring-kafka
    implementation("org.springframework.kafka:spring-kafka")
}