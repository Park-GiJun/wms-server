# wms-server

가상 창고관리(WMS) 백엔드. **MSA 멀티모듈**(Spring Cloud: Eureka + Gateway + Config)이며 각 피처
서비스는 내부적으로 **헥사고날(Ports & Adapters) + CQRS** 로 구성된다. 프론트엔드 없음(백엔드 전용).

## 확장 철학 — 재고원장 척추

WMS 의 모든 물리 이동(입고·적치·이동·피킹·패킹·출고·조정·실사)은 **`StockMovement` 이벤트 하나로
환원**된다. 이걸 **append-only 재고원장**으로 두고, `(item, location)` 단위 **단일 라이터**로 음수재고/
오버피킹을 차단한다. **새 기능 = 이 이벤트를 발행/구독하는 피처 서비스 추가** — 척추(master)는 안 건드린다.

## 모듈 (포트 대역 191xx)

| 모듈                  | 포트    | 역할                                                          | 상시 |
|---------------------|-------|-------------------------------------------------------------|----|
| `platform-server`   | 19159 | Config(native) + Eureka 통합 한 JVM. 가장 먼저 기동                  | O  |
| `gateway`           | 19100 | 유일한 외부 진입점 + 단일 인증 지점(JWT 검증)                               | O  |
| `shared`            | —     | 실행 불가 `java-library`. JWT 검증기·공통 응답/예외·`StockMovementEvent` | —  |
| `master-service`    | 19102 | **★척추.** 재고원장·`stock.movement` 발행 + 품목·로케이션·거래처 마스터           | O  |
| `user-service`      | 19103 | 신원(user) 마스터 + **JWT 발급**                                    | O  |
| `notification-service` | 19104 | 알림 — `stock.movement` 등 이벤트 구독                             | O  |

추가 피처(`inbound` / `outbound` / `cycle-count` …)는 **`/new-service <name>`** 로 붙인다.

## 기동 순서

```powershell
cd backend
.\gradlew.bat :platform-server:bootRun   # 1. Config+Eureka (가장 먼저)
.\gradlew.bat :gateway:bootRun           # 2. 게이트웨이
.\gradlew.bat :master-service:bootRun    # 3. 척추(재고원장+마스터)
.\gradlew.bat :user-service:bootRun      # 4. 신원/인증 (JWT 발급)
```

인프라(Postgres/Redis/Kafka)는 홈서버 공유 인스턴스를 쓴다 — repo 루트 `.env`(미추적, `.env.example`
참고)로 접속 정보를 주입한다. datasource·JWT_SECRET 은 fallback 이 없어 값이 없으면 부팅되지 않는다.
순수 로컬 인프라가 필요하면:

```powershell
docker compose -f ..\infra\compose.yaml up -d
```
