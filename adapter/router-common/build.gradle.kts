dependencies {
    // Connect the dependencies among the modules of this project
    api(project(":commons:common"))
}

// 해당 module은 테스트 대상에서 제외한다
tasks.withType<Test> {
    exclude("**/applicationcommon/**")
}