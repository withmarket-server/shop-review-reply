dependencies {
    // Connect the dependencies among the modules of this project
    api(project(":adapter:elasticsearch"))
    api(project(":port:service-command"))
    api(project(":port:event-interface"))

    // spring-kafka
    implementation("org.springframework.kafka:spring-kafka")

    // Spring Elasticsearch
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
}

// 해당 module은 테스트 대상에서 제외한다
tasks.withType<Test> {
    exclude("**/applicationkafka/**")
}