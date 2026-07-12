plugins {
    // 입고 피처: 입고지시·검수 후 RECEIPT/PUTAWAY 커맨드를 척추(master-service)로 보낸다.
    // 헥사고날 + CQRS, DB-per-service → JPA.
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

    // stock.movement 발행/구독 · read model/캐시.
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
