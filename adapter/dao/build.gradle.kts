import org.jetbrains.kotlin.builtins.StandardNames.FqNames.annotation

dependencies {
    // Connect dependencies among the modules of this project
    api(project(":domain:dynamo"))
    api(project(":port:repository"))
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("software.amazon.awssdk:dynamodb-enhanced:2.17.191")
}

// 해당 module은 테스트 대상에서 제외한다
tasks.withType<Test> {
    // exclude("**/dao/**")
}