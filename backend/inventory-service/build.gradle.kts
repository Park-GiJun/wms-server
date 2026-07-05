plugins {
    // ★척추 = 재고원장(append-only 이동)·재고/로케이션 투영·stock.movement 발행. 헥사고날 + CQRS.
    // (item, location) 단위 단일 라이터로 음수재고/오버피킹 차단 — trading 의 심볼별 단일 라이터 매칭과 동형.
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

    // 이동 이벤트 발행/구독(피처 서비스가 이동 커맨드→척추, 척추가 stock.movement 발행) · read model/캐시.
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // 스키마 마이그레이션(classpath db/migration 의 V__*.sql).
    implementation("org.flywaydb:flyway-core")
    runtimeOnly("org.flywaydb:flyway-database-postgresql")

    // dev=H2(PostgreSQL 호환 모드), 운영=PostgreSQL.
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}
