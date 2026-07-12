package com.gijun.wms.inbound

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

/**
 * inbound 피처 서비스. 헥사고날 + CQRS, DB-per-service(PostgreSQL, 스키마=Flyway).
 * gateway 가 검증한 X-User-* 헤더를 신뢰한다. 필요 시 stock.movement 를 발행/구독한다.
 */
@ConfigurationPropertiesScan
@SpringBootApplication
class InboundServiceApplication

fun main(args: Array<String>) {
    runApplication<InboundServiceApplication>(*args)
}
