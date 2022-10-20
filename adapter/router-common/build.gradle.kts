dependencies {
    // Connect the dependencies among the modules of this project
    api(project(":commons:common"))
}

// 해당 module은 테스트 대상에서 제외한다
tasks.withType<Test> {
    exclude("**/applicationcommon/**")
}

// root folder의 gradle project는 빌드하지 않는다
tasks {
    named<Jar>("jar") {
        enabled = true
    }

    named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
        enabled = false
    }
}