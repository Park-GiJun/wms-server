plugins {
    // 신원(user) 마스터 + 인증. 헥사고날 + CQRS, DB-per-service → JPA.
    // 신원 마스터를 소유하므로 이 서비스가 JWT 를 발급한다(검증은 gateway). 발급 책임을 옮기려면
    // 아래 security-crypto/jjwt 런타임 의존성과 config-repo/user-service.yml 의 jwt 블록을 함께 이동.
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
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

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    // 부팅 시 platform-server(Config)에서 설정을 가져온다(spring.config.import=configserver:).
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation(libs.kotlin.logging)
    implementation(project(":shared"))

    // Swagger UI — dev 에서 http://localhost:19103/swagger-ui.html
    implementation(libs.springdoc.webmvc.ui)

    // 비밀번호 해싱만 필요 — 풀 security 스타터 대신 crypto 모듈만 (필터 체인 없음).
    implementation("org.springframework.security:spring-security-crypto")
    // JWT 발급(서명)은 user(신원 소유) 책임. shared 는 api 만 노출하므로 impl/jackson 을 직접 런타임에 둔다.
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)

    // 사용자 변경 이벤트 발행(권한 캐시 무효화 등) · read model/캐시.
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // 스키마 마이그레이션(classpath db/migration 의 V__*.sql).
    implementation("org.flywaydb:flyway-core")
    runtimeOnly("org.flywaydb:flyway-database-postgresql")

    // PostgreSQL(DB-per-service). H2 는 테스트 전용.
    runtimeOnly("org.postgresql:postgresql")
    testRuntimeOnly("com.h2database:h2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}
