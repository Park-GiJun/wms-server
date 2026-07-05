# CLAUDE.md

이 파일은 Claude Code(claude.ai/code)가 이 저장소에서 작업할 때의 가이드다.

## 프로젝트 개요

**wms-server — 가상 창고관리(WMS) 백엔드.** 프론트엔드 없음(백엔드 전용). **MSA 멀티모듈**
(Spring Cloud: Eureka + Gateway + Config)이며, 각 피처 서비스는 내부적으로 **헥사고날(Ports &
Adapters) + CQRS** 로 구성된다. (컨벤션·인프라는 sibling 프로젝트 trading-server / ticket-server /
rally 와 동일하게 승계하고 **도메인만 WMS 로 교체**.)

- Kotlin / JDK 25 (Gradle toolchain) / Spring Boot 4.x / Spring Cloud 2025.1.x
- 영속성: JPA(Hibernate) + PostgreSQL (홈서버 공유 인스턴스, DB-per-service), 스키마는 Flyway
- 부가 인프라: Redis(read model·캐시·멱등), Kafka(재고이동 스트림)

## 확장 철학 — 재고원장 척추 (가장 중요)

WMS 의 모든 물리 이동(입고·적치·이동·피킹·패킹·출고·조정·실사)은 **`StockMovement` 이벤트 하나로
환원**된다(`shared/.../event/StockMovementEvent.kt`). 이걸 **append-only 재고원장**으로 두고,
`(item, location)` 단위 **단일 라이터**로 음수재고/오버피킹을 차단한다. trading-server 의 심볼별 단일
라이터 매칭엔진(`Order → Trade`)과 동형이다.

> **확장 규칙: 새 기능 = `stock.movement` 를 발행(커맨드)하거나 구독(투영)하는 피처 서비스 추가.**
> 척추(`master-service`)는 건드리지 않는다. 새 피처는 **`/new-service <name>`** 로 스캐폴드한다.

## 모노레포 구조

```
wms-server/
├─ backend/      # Gradle 멀티모듈 빌드루트 (gradlew, settings.gradle.kts 가 여기 — 저장소 루트 아님)
│                #   + Dockerfile(ARG MODULE 로 모듈별 이미지 빌드)
├─ config-repo/  # platform-server(Config native)가 읽는 전 서비스 중앙 설정
├─ infra/        # compose.yaml (로컬 dev Postgres/Redis/Kafka — sibling 프로젝트와 동일 인프라)
├─ deploy/       # 배포 compose(docker-compose.yml) + .env.example (실제 .env 는 미추적)
└─ .teamcity/    # TeamCity Kotlin DSL(settings.kts) — 서비스별 수동 빌드·배포 BuildType
```

## 배포 / CI (홈서버 · TeamCity)

`.teamcity/settings.kts`(Versioned Settings, Kotlin DSL)가 모듈별 **수동 실행** 배포 BuildType 을
생성한다. 각 BuildType 은 `deploy/docker-compose.yml` 로 해당 서비스 이미지를 빌드 후 `--no-deps` 로
무중단 교체한다(레지스트리 없음 — 빌드=배포 동일 호스트). 새 피처를 추가하면 `settings.kts` 의
`backendServices` 목록에도 한 줄 넣는다. 배포 시크릿은 서버의 `deploy/.env`(미추적)로 주입하고,
공유 인프라(`infra-postgres`/`infra-redis`/`kafka`, 네트워크 `infra-net`)와
`wms_master`/`wms_notification` DB 는 미리 존재해야 한다.

## 백엔드 모듈 (포트 대역 191xx)

> **포트 정책:** 홈서버 다수 프로젝트 공존 → wms 는 **`191xx` 대역**을 쓴다
> (ticket `180xx`, rally `188xx`, trading `189xx` 재사용 금지). 호스트 노출은 `gateway` 뿐.

| 모듈                  | 포트    | 역할                                                                    | 상시 |
|---------------------|-------|-----------------------------------------------------------------------|----|
| `platform-server`   | 19159 | Config(native) + Eureka 통합 한 JVM. 자기 등록 안 함. 가장 먼저 기동                 | O  |
| `gateway`           | 19100 | 유일한 외부 진입점 + 단일 인증 지점(JWT 검증) + 라우팅                                   | O  |
| `shared`            | —     | 실행 불가 `java-library`. JwtTokenValidator·공통 응답/예외·`StockMovementEvent` | —  |
| `master-service`    | 19102 | **★척추.** 재고원장(append-only)·`stock.movement` 발행 + 품목·로케이션·거래처·신원(user) 마스터 + **JWT 발급** | O  |
| `notification-service` | 19104 | 알림 — `stock.movement` 등 이벤트 구독                                       | O  |

모든 모듈은 패키지 루트 `com.gijun.wms` 를 공유한다(모듈이 달라도 같은 베이스 패키지).
추가 피처(`inbound-service` / `outbound-service` …)는 `/new-service` 로 191xx 다음 포트에 붙인다.

## 인증 아키텍처 (JWT)

JWT **발급은 master-service**(신원 마스터 소유), **검증은 gateway**. 양쪽은 `shared` 의
`JwtTokenValidator` 와 동일한 `jwt.secret`/`jwt.issuer` 를 공유한다.

1. `gateway` 가 모든 요청의 `Authorization: Bearer` 를 검증한다.
2. 성공 시 신원을 **`X-User-Id` / `X-User-Email` / `X-User-Role` 헤더로 백엔드에 전달**한다.
   클라이언트가 보낸 `X-User-*` 는 신뢰하지 않고 게이트웨이가 덮어쓰거나 제거한다.
3. 공개 경로(`/api/auth/**`, `/actuator/**`)는 인증 없이 통과한다.
4. 백엔드 서비스는 게이트웨이가 보장한 `X-User-*` 헤더를 신뢰한다.

JWT secret/issuer 변경 시 **gateway 와 master-service 의 설정을 함께** 수정한다.

## 서비스 내부 구조 (헥사고날 + CQRS)

각 피처 서비스는 동일한 레이어 규칙을 따른다. 의존성 화살표는 항상 **바깥 → 안쪽**.
domain/application 은 infrastructure 를 모른다.

```
domain/            순수 Kotlin. model / service / enums / exception(sealed)
application.<도메인>/
  port.in/  command/ · query/       유스케이스 인터페이스 (1 인터페이스 = 1 함수)
  port.out/ persistence/ · message/ · cache/ · token/ · security/   기술 관심사별
  dto/      command/ · query/ · result/ · event/
  handler/  command/ · query/       (CommandHandler / QueryHandler)
infrastructure/
  adapter.in.<도메인>.web/          REST 컨트롤러 + 요청/응답 DTO
  adapter.out.<도메인>/             포트 구현체 (persistence / message / cache / token / security)
  config/                           예외 핸들러, security 등
```

- **CQRS**: 명령은 `@Transactional`, 조회는 `@Transactional(readOnly = true)`.
- **`port.out` 은 기술 관심사(persistence/message/cache/token/security)로** 나눈다(읽기/쓰기 아님).
- **`master-service`(척추) 예외:** 재고원장 단일 라이터 코어는 성능·정합성상 domain 안에 두고,
  입력(이동 커맨드)·출력(`stock.movement` 발행)만 port 로 추상화한다.

## 빌드 & 실행 명령

**반드시 빌드 루트 `backend/` 에서 실행**(또는 `-p backend`). `JAVA_HOME` 이 비어 있으면 JDK 25 를 지정.

```powershell
.\gradlew.bat build                          # 전체 빌드
.\gradlew.bat :platform-server:bootRun        # 1. Config+Eureka (가장 먼저)
.\gradlew.bat :gateway:bootRun                # 2.
.\gradlew.bat :master-service:bootRun         # 3. 척추(재고원장+마스터+신원/JWT 발급)
.\gradlew.bat test
```

### 로컬 인프라 & 중앙 설정

로컬 dev 는 홈서버 공유 인프라(Postgres/Redis/Kafka)를 그대로 쓴다 — repo 루트 `.env`(미추적)가
datasource/redis/kafka/jwt 를 주입한다(`<SVC>_DB_URL`/`DB_USERNAME`/`REDIS_*`/
`KAFKA_BOOTSTRAP_SERVERS`/`JWT_SECRET`). **DB URL 은 서비스별 변수**(`MASTER_DB_URL` 등,
DB-per-service 라 공유 불가)이고 **datasource·JWT_SECRET 은 fallback 이 없다** — 값이 없으면
부팅 실패가 정상이다. `infra/compose.yaml` 은 순수 로컬 인프라가 필요할 때만.
**실제 자격증명은 절대 커밋하지 않는다**(public 저장소). 배포는 `deploy/.env`(미추적)로 주입.

`config-repo/`: `application.yml`(공통) + `{service}.yml` + `application-prod.yml`(prod 오버라이드).
값은 환경변수 주입(주소성 값만 `${ENV:기본값}`, 자격증명·시크릿은 fallback 금지), 시크릿 커밋 금지
— config-repo 는 **구조**만 담는다.
각 서비스는 부팅 시 platform-server(19159)에서 설정을 가져오므로 platform-server 가 가장 먼저.

## 새 피처 서비스 추가

`.claude/commands/new-service.md` 의 **`/new-service <name>`** 슬래시 커맨드가:
settings 등록 · `backend/<name>-service`(build/Application/application.yml) · `config-repo/<name>-service.yml`
· gateway 라우트 · Flyway 베이스라인까지 컨벤션대로 스캐폴드한다. 상세는 그 파일 참고.
