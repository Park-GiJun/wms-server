package com.gijun.wms.notification.infrastructure.adapter.`in`.message

import com.gijun.wms.shared.event.StockMovementEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

/**
 * stock.movement 첫 컨슈머 — 파이프라인 검증용 로그만 남긴다.
 * 실제 알림 규칙(안전재고 미달 등)이 생기면 유스케이스 호출로 교체하고,
 * 멱등 처리는 idem:{movementId} (Redis) 로 붙인다.
 */
@Component
class StockMovementEventListener {

    @KafkaListener(topics = ["stock.movement"])
    fun on(event: StockMovementEvent) {
        log.info {
            "stock.movement 수신: movementId=${event.movementId} sku=${event.skuId} " +
                    "location=${event.locationId} type=${event.type} qty=${event.qty} seq=${event.seq}"
        }
    }
}
