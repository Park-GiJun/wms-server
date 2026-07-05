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
    // 로컬 dev 전용 .env 자동 로드(운영 jar 미포함).
    developmentOnly(libs.springboot4.dotenv)

    // 단일 외부 진입점 + 단일 인증 지점. WebFlux 기반 reactive 게이트웨이.
    implementation("org.springframework.cloud:spring-cloud-starter-gateway-server-webflux")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    // 부팅 시 platform-server(Config)에서 설정을 가져온다(spring.config.import=configserver:).
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // JwtTokenValidator·AuthHeaders 공유
    implementation(project(":shared"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}
