dependencies {
    // Connect dependencies among the modules of this project
    api(project(":domain:dynamo"))
    api(project(":port:repository"))
    api(project(":adapter:dao"))

    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
}

// reader에 대해서는 테스트하지 않는다
tasks.withType<Test> {
    exclude("**/reader/**")
}