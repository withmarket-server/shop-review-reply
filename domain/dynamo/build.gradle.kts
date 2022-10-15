dependencies {
    // Connect dependency among the modules
    api(project(":commons:common"))

    implementation("software.amazon.awssdk:dynamodb-enhanced:2.17.191")
}

// DynamoDbBean에 대해서 allOpen plugin을 이용해 final을 제거한다
allOpen {
    annotation("software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean")
}

// DynamoDbBean 어노테이션이 적용된 entity에 대해서 parameter가 없는 생성자를 만들어준다
noArg {
    annotation("software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean")
}

// 해당 module은 테스트 대상에서 제외한다
tasks.withType<Test> {
    exclude("**/dynamo/**")
}