dependencies {
    // Connect the dependendies among the modules of this project
    api(project(":adapter:kafka-config")) // dependency in adapter layer for connect kafka
    api(project(":adapter:router-common"))
    api(project(":port:service-command"))
    api(project(":port:event-interface")) // port about kafka

    // spring-kafka
    implementation("org.springframework.kafka:spring-kafka")
}