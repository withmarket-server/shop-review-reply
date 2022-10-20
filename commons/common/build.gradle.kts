dependencies {

}

// 해당 module은 테스트 대상에서 제외한다
tasks.withType<Test> {
    exclude("**/common/**")
}

// 해당 모듈은 빌드 대상에서 제외한다
tasks {
    named<Jar>("jar") {
        enabled = true
    }

    named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
        enabled = false
    }
}