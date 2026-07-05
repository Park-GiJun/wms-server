package com.gijun.wms.user

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

/**
 * 신원(user 마스터) + 인증 피처 서비스. 헥사고날 + CQRS,
 * DB-per-service(PostgreSQL, 스키마=Flyway).
 * 신원 마스터를 소유하므로 **JWT 발급은 여기서**(검증은 gateway, shared 의 JwtTokenValidator 공유).
 */
@ConfigurationPropertiesScan
@SpringBootApplication
class UserServiceApplication

fun main(args: Array<String>) {
    runApplication<UserServiceApplication>(*args)
}
