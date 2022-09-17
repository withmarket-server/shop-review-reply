apply(plugin = "com.google.protobuf")

dependencies {
    // Connect the dependendies among the modules of this project
    api(project(":adapter:kafka-config")) // dependency in adapter layer for connect kafka
    api(project(":adapter:router-common"))
    api(project(":port:service-command"))
    api(project(":port:event-interface")) // port about kafka

    // 외부 라이브러리 import
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // spring-kafka
    implementation("org.springframework.kafka:spring-kafka")

    // gRPC
    api("io.grpc:grpc-kotlin-stub:1.3.0")
    api("io.grpc:grpc-protobuf:1.49.0")
    api("io.grpc:grpc-netty-shaded:1.49.0")
}