rootProject.name = "wms"

// edge / 공통
include("shared")
// config(Spring Cloud Config) + discovery(Eureka) 를 한 JVM 으로 묶은 플랫폼 서버 (포트 19159)
include("platform-server")
include("gateway")

// ── 피처 서비스 ───────────────────────────────────────────────────────────────
// 척추 = master(재고원장·append-only·이동 이벤트 발행 + 품목·로케이션·거래처 마스터).
// 나머지 피처는 /new-service 로 붙인다.
include("master-service")      // ★척추: 재고원장 + stock.movement 발행 + 품목·로케이션·거래처·신원(user) 마스터 + JWT 발급
include("notification-service") // 알림 — stock.movement 등 이벤트 구독
include("inbound-service")     // 입고 — 입고지시·검수 후 RECEIPT/PUTAWAY 커맨드를 척추로 보낸다
include("outbound-service")    // 출고 — 주문·피킹·패킹·출고(PICK/PACK/SHIP) 커맨드를 척추로 보낸다
