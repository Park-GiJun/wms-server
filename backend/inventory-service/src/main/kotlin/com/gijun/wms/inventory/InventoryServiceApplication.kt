package com.gijun.wms.inventory

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

/**
 * ★척추 = 재고원장(append-only 이동)·재고/로케이션 투영·stock.movement 이벤트 발행.
 * (item, location) 단위 단일 라이터로 음수재고/오버피킹을 차단한다.
 * 헥사고날 + CQRS, DB-per-service(dev=H2, 운영=PostgreSQL, 스키마=Flyway).
 * gateway 가 검증한 X-User-* 헤더를 신뢰한다(자체 인증 없음).
 */
@ConfigurationPropertiesScan
@SpringBootApplication
class InventoryServiceApplication

fun main(args: Array<String>) {
    runApplication<InventoryServiceApplication>(*args)
}
