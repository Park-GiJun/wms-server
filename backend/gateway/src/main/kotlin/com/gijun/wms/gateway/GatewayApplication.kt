package com.gijun.wms.gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

/**
 * wms 의 유일한 외부 진입점 + 단일 인증 지점.
 * 모든 요청의 JWT 를 검증하고 신원을 X-User-* 헤더로 백엔드에 전파한다(필터는 별도 작성).
 * 라우팅은 platform-server(Config)의 config-repo/gateway.yml 에서 가져온다.
 */
@ConfigurationPropertiesScan
@SpringBootApplication
class GatewayApplication

fun main(args: Array<String>) {
    runApplication<GatewayApplication>(*args)
}
