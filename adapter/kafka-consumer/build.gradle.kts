dependencies {
    // Connect the dependencies among the modules of this project
    api(project(":port:service-command"))
    api(project(":port:event-interface"))

    // spring-kafka
    implementation("org.springframework.kafka:spring-kafka")
}