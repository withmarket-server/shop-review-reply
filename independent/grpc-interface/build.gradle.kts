import com.google.protobuf.gradle.*

val grpcVersion = "3.21.5"

apply(plugin = "com.google.protobuf")

configurations.forEach {
    if (it.name.toLowerCase().contains("proto")) {
        it.attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, "java-runtime"))
    }
}

dependencies {
    compileOnly("javax.annotation:javax.annotation-api:1.3.2")

    // grpc
    api("com.google.protobuf:protobuf-java-util:$grpcVersion")
    api("io.grpc:grpc-kotlin-stub:1.3.0")
    api("io.grpc:grpc-protobuf:1.49.0")
    api("io.grpc:grpc-netty-shaded:1.49.0")
}

protobuf {
    generatedFilesBaseDir = "$projectDir/build/generated/source"
    protoc {
        artifact = "com.google.protobuf:protoc:$grpcVersion"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.34.0"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.0.0:jdk7@jar"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
            it.generateDescriptorSet = true
            it.descriptorSetOptions.includeSourceInfo = true
            it.descriptorSetOptions.includeImports = true
            it.descriptorSetOptions.path = "$buildDir/resources/META-INF/armeria/grpc/service-name.dsc"
        }
    }
}

sourceSets {
    main {
        java.srcDir("build/generated/source/main/grpckt")
        java.srcDir("build/generated/source/main/grpc")
        java.srcDir("build/generated/source/main/java")
    }
}

// DuplicatesStrategy 설정
tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

// 해당 module은 테스트 대상에서 제외한다
tasks.withType<Test> {
    exclude("**/grpcinterface/**")
}