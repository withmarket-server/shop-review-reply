dependencies {
    // Connect the dependendies among the modules of this project
    api(project(":port:service-command"))
    api(project(":port:event-interface")) // port about kafka
    api(project(":adapter:kafka-config")) // dependency in adapter layer for connect kafka

    // spring-kafka
    implementation("org.springframework.kafka:spring-kafka")
}