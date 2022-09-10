dependencies {
    // Connect the dependencies among the modules of this project
    api(project(":adapter:kafka-config")) // dependency of adapter layer for connecting to kafka
    api(project(":adapter:router-common"))
    api(project(":port:client-query"))
    api(project(":port:service-query"))
    api(project(":port:event-interface"))

    // spring-kafka
    implementation("org.springframework.kafka:spring-kafka")
}