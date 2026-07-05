package com.gijun.wms.platform

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.config.server.EnableConfigServer
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

/**
 * wms 플랫폼 서버 = **Spring Cloud Config(native) + Eureka 를 한 JVM 으로 묶었다**(홈서버 JVM 수 절감).
 *
 * - Config: native(파일시스템) 백엔드로 저장소 루트의 `config-repo/` 를 설정 소스로 노출한다.
 *   각 서비스는 부팅 시 여기서 설정을 가져오므로 전체 스택에서 **가장 먼저** 떠야 한다.
 * - Eureka: 단일 노드 레지스트리. 자기 자신은 등록/페치하지 않는다.
 *
 * 자기 자신은 config 를 import 하지 않고(부트스트랩 순환 방지) 본인 application.yml 만 읽는다.
 * 기동 순서: **platform-server → gateway → master-service → (추가 피처)**.
 */
@EnableConfigServer
@EnableEurekaServer
@SpringBootApplication
class PlatformServerApplication

fun main(args: Array<String>) {
    runApplication<PlatformServerApplication>(*args)
}
