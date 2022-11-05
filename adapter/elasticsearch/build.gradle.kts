dependencies {
    // Domain의 VO를 임시로 가져오기 위한 의존
    api(project(":domain:dynamo"))

    // Spring Elasticsearch
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
}

tasks {
    named<Jar>("jar") {
        enabled = true
    }

    named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
        enabled = false
    }
}

// 해당 module은 테스트 대상에서 제외한다
tasks.withType<Test> {
    exclude("**/elasticsearch/**")
}