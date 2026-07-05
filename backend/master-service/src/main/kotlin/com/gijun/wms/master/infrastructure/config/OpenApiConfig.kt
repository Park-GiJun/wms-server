package com.gijun.wms.master.infrastructure.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Swagger UI (dev: http://localhost:19102/swagger-ui.html).
 * 운영 진입은 gateway 뿐이므로 UI 는 dev 에서 서비스 포트로 직접 연다.
 * bearerAuth 는 gateway 경유 호출용 — 서비스 직접 호출 시엔 X-User-* 헤더 파라미터를 채운다.
 */
@Configuration
class OpenApiConfig {

    @Bean
    fun openApi(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("wms master-service API")
                .description("재고원장·품목/로케이션/거래처 마스터 + 신원(user)/인증. register → admin confirm → login(JWT 발급).")
                .version("v1"),
        )
        .components(
            Components().addSecuritySchemes(
                "bearerAuth",
                SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT"),
            ),
        )
        .addSecurityItem(SecurityRequirement().addList("bearerAuth"))
}
