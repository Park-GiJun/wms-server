---
description: WMS 피처 서비스(MSA 모듈)를 컨벤션대로 스캐폴드한다
argument-hint: <name> (예: inbound — "-service" 접미사·포트·패키지는 자동)
allowed-tools: Read, Write, Edit, Bash, Glob
---

# /new-service — 새 WMS 피처 서비스 스캐폴드

`wms-server` 컨벤션에 맞춰 새 MSA 피처 서비스 모듈 하나를 **도메인 코드 없이 골격만** 생성한다.
서비스는 게이트웨이를 통해 인증된 `X-User-*` 헤더를 신뢰하고, 필요 시 `stock.movement`(척추 이벤트)를
발행/구독하는 피처다. **척추(inventory-service)는 절대 수정하지 않는다.**

## 입력

- 서비스 이름: **`$1`** (예: `inbound`, `outbound`, `cycle-count`). 접미사 `-service` 는 붙이지 않는다.
  - 없으면 사용자에게 물어본다.
  - 모듈 디렉터리 = `<name>-service`, 패키지 = `com.gijun.wms.<nameCamel>`
    (하이픈 제거·camelCase. 예: `cycle-count` → 패키지 `com.gijun.wms.cyclecount`),
    클래스 = `<NamePascal>ServiceApplication` (예: `CycleCountServiceApplication`).

## 절차 (순서대로)

### 0. 사전 점검
- `backend/<name>-service/` 가 이미 있으면 중단하고 사용자에게 알린다(덮어쓰기 금지).
- **다음 빈 포트 산정:** `config-repo/*.yml` 의 `server.port` 를 모두 읽어(`Glob` + `Read`/`Grep`)
  191xx 중 가장 큰 값 + 1 을 쓴다. (현재 사용: platform 19159, gateway 19100, inventory 19101,
  master 19102 → 신규는 보통 **19103** 부터.) 19159 는 platform 예약이니 건너뛴다.

### 1. `backend/<name>-service/build.gradle.kts` 생성
아래 템플릿(inventory-service 와 동일한 JPA+Kafka+Redis+Flyway 조합). 주석의 도메인 설명만 `$1` 에 맞게.

```kotlin
plugins {
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
    developmentOnly(libs.springboot4.dotenv)

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation(libs.kotlin.logging)
    implementation(project(":shared"))

    // stock.movement 발행/구독 · read model/캐시.
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    implementation("org.flywaydb:flyway-core")
    runtimeOnly("org.flywaydb:flyway-database-postgresql")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}
```

> JWT **발급**이 필요한 서비스라면(보통 불필요 — 발급은 master-service 단일 지점) master-service
> build 처럼 `spring-security-crypto` + `jjwt.impl`/`jjwt.jackson` 런타임을 추가한다. 기본은 넣지 않는다.

### 2. `backend/<name>-service/src/main/kotlin/com/gijun/wms/<pkg>/<Pascal>ServiceApplication.kt`

```kotlin
package com.gijun.wms.<pkg>

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

/**
 * <$1> 피처 서비스. 헥사고날 + CQRS, DB-per-service(dev=H2, 운영=PostgreSQL, 스키마=Flyway).
 * gateway 가 검증한 X-User-* 헤더를 신뢰한다. 필요 시 stock.movement 를 발행/구독한다.
 */
@ConfigurationPropertiesScan
@SpringBootApplication
class <Pascal>ServiceApplication

fun main(args: Array<String>) {
    runApplication<<Pascal>ServiceApplication>(*args)
}
```

### 3. `backend/<name>-service/src/main/resources/application.yml` (부트스트랩 — 식별자+주소만)

```yaml
spring:
  application:
    name: <name>-service
  config:
    import: optional:configserver:${CONFIG_SERVER_URL:http://localhost:19159}
```

### 4. `config-repo/<name>-service.yml` (중앙 설정 — 포트/DB/kafka)

```yaml
server:
  port: <PORT>

spring:
  datasource:
    url: ${DB_URL:jdbc:h2:mem:wms_<pkg>;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE}
    username: ${DB_USERNAME:sa}
    password: ${DB_PASSWORD:}
  kafka:
    consumer:
      group-id: ${KAFKA_GROUP_<UPPER>:wms-<name>}
```

### 5. `backend/<name>-service/src/main/resources/db/migration/V1__init.sql` (Flyway 베이스라인)

```sql
-- <$1>-service 스키마 시작점. 도메인 설계 확정 후 V2__*.sql 부터 이어서 추가한다.
SELECT 1;
```

### 6. `backend/settings.gradle.kts` 에 등록
`Edit` 로 include 목록에 한 줄 추가:
```kotlin
include("<name>-service")
```

### 7. `config-repo/gateway.yml` 에 라우트 추가
`spring.cloud.gateway.server.webflux.routes` 리스트에 `Edit` 로 항목 추가(들여쓰기 유지):
```yaml
            - id: <name>-core
              uri: lb://<name>-service
              predicates:
                - Path=/api/<name>/**
```

### 8. 마무리 안내(사용자에게 출력)
- 새 모듈을 Gradle 이 인식하도록 IDE 를 새로고침(또는 `.\gradlew.bat :<name>-service:build`).
- **gateway 라우트 변경은 `platform-server`(Config) 재시작 후 gateway 재시작**해야 반영된다.
- 생성 요약: 모듈명 / 포트 / 패키지 / 추가된 라우트 경로를 표로 보고한다.
- 도메인 코드(entity/handler/adapter)는 사용자가 채운다 — 이 커맨드는 골격까지만.

**주의:** 도메인 로직·엔티티·컨트롤러는 만들지 않는다. 위 8단계 파일/등록만 생성하고, 기존 모듈
(특히 inventory-service 척추)은 수정하지 않는다.
