package com.gijun.wms.master

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

/**
 * 품목·로케이션·거래처 마스터 + 신원(user 마스터). 헥사고날 + CQRS,
 * DB-per-service(dev=H2, 운영=PostgreSQL, 스키마=Flyway).
 * 신원 마스터를 소유하므로 **JWT 발급은 여기서**(검증은 gateway, shared 의 JwtTokenValidator 공유).
 */
@ConfigurationPropertiesScan
@SpringBootApplication
class MasterServiceApplication

fun main(args: Array<String>) {
    runApplication<MasterServiceApplication>(*args)
}
