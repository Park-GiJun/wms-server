plugins {
    // 실행 불가능한 공유 라이브러리. bootJar 없이 일반 jar 만 만든다.
    `java-library`
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.springDependencyManagement)
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:${libs.versions.springBoot.get()}")
    }
}

dependencies {
    api("org.springframework.boot:spring-boot-starter")
    // HttpStatus 등 HTTP 추상화(ErrorCode). spring-web 만 — 서블릿/WebFlux 컨테이너는 끌어오지 않으므로
    // WebFlux 인 gateway 와도 충돌하지 않는다.
    api("org.springframework:spring-web")
    api("com.fasterxml.jackson.module:jackson-module-kotlin")

    // JWT 검증기 (gateway·user-service 가 동일 secret/issuer 로 공유)
    api(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)

    api(libs.kotlin.logging)

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}
