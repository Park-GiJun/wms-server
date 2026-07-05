plugins {
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.springBoot)
    alias(libs.plugins.springDependencyManagement)
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${libs.versions.springCloud.get()}")
    }
}

dependencies {
    // config + discovery 를 한 JVM 으로 묶은 플랫폼 서버(홈서버 JVM 수 절감).
    // 둘 다 Eureka 에 자기등록하지 않는 edge 라 합쳐도 충돌 없음. Eureka·Config 는 같은 MVC
    // 컨텍스트(단일 포트 19159)에서 서빙된다. 전체 스택에서 가장 먼저 떠야 한다.
    implementation("org.springframework.cloud:spring-cloud-config-server")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}
