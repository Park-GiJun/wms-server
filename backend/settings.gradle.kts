rootProject.name = "wms"

// edge / 공통
include("shared")
// config(Spring Cloud Config) + discovery(Eureka) 를 한 JVM 으로 묶은 플랫폼 서버 (포트 19159)
include("platform-server")
include("gateway")

// ── 피처 서비스 ───────────────────────────────────────────────────────────────
// 척추 = inventory(재고원장·append-only·이동 이벤트 발행). 나머지 피처는 /new-service 로 붙인다.
include("inventory-service")   // ★척추: 재고원장 + 재고/로케이션 투영 + stock.movement 발행
include("master-service")      // 품목·로케이션·거래처 마스터 + 신원/JWT 발급
// include("inbound-service")   // /new-service inbound  로 생성
// include("outbound-service")  // /new-service outbound 로 생성
