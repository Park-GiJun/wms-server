package com.gijun.wms.master

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

/**
 * ★척추 = 재고원장(append-only 이동)·재고/로케이션 투영·stock.movement 이벤트 발행
 * + 품목·로케이션·거래처 마스터(구 inventory-service 통합)
 * + 신원(user) 마스터·**JWT 발급**(구 user-service 통합 — 검증은 gateway, shared 의 JwtTokenValidator 공유).
 * (item, location) 단위 단일 라이터로 음수재고/오버피킹을 차단한다.
 * 헥사고날 + CQRS, DB-per-service(PostgreSQL, 스키마=Flyway).
 */
@ConfigurationPropertiesScan
@SpringBootApplication
class MasterServiceApplication

fun main(args: Array<String>) {
    runApplication<MasterServiceApplication>(*args)
}
