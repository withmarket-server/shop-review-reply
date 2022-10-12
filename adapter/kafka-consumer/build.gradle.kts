dependencies {
    // Connect the dependencies among the modules of this project
    api(project(":adapter:dao"))
    api(project(":port:event-interface"))
    api(project(":port:repository"))

    // spring-kafka
    implementation("org.springframework.kafka:spring-kafka")
}