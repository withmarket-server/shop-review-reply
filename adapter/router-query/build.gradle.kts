dependencies {
    api(project(":adapter:router-common"))
    api(project(":port:client-query"))
    api(project(":port:service-query"))

    // 외부 라이브러리 import
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // spring-kafka
    implementation("org.springframework.kafka:spring-kafka")

    // 해당 dependency가 있어야 coroutine을 이용해서 grpc를 이용 가능하다
    api("io.grpc:grpc-kotlin-stub:1.3.0")

    // Armeria
    implementation("com.linecorp.armeria:armeria-grpc")
    implementation("com.linecorp.armeria:armeria-spring-boot2-webflux-starter")
}

dependencyManagement {
    imports {
        mavenBom("com.linecorp.armeria:armeria-bom:0.99.9")
        mavenBom("io.netty:netty-bom:4.1.51.Final")
    }
}