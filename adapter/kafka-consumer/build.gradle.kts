dependencies {
    // Connect the dependencies among the modules of this project
    api(project(":port:service-command"))
    api(project(":port:event-interface"))

    // spring-kafka
    implementation("org.springframework.kafka:spring-kafka")
}

// 해당 module은 테스트 대상에서 제외한다
tasks.withType<Test> {
    exclude("**/applicationkafka/**")
}