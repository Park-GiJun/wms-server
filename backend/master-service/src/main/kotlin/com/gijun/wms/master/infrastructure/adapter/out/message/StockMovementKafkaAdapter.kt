package com.gijun.wms.master.infrastructure.adapter.out.message

import com.gijun.wms.master.application.port.out.message.PublishStockMovementPort
import com.gijun.wms.shared.event.StockMovementEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager

private val log = KotlinLogging.logger {}

/**
 * stock.movement Kafka 발행 어댑터. 트랜잭션 안에서 호출되면 **커밋 후(afterCommit)** 발행을
 * 예약한다 — 롤백된 이동은 발행되지 않는다. "커밋됐는데 발행 전 크래시" 갭은 알려진 트레이드오프로,
 * 유실이 문제가 되면 outbox 패턴으로 승격한다.
 * key = "{skuId}:{locationId}" — 같은 (SKU, 로케이션) 쌍은 같은 파티션에서 순서 보장.
 */
@Component
class StockMovementKafkaAdapter(
    private val kafkaTemplate: KafkaTemplate<String, StockMovementEvent>,
    @param:Value("\${inventory.topic.stock-movement}") private val topic: String,
) : PublishStockMovementPort {

    override fun publish(event: StockMovementEvent) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronization {
                override fun afterCommit() = send(event)
            })
        } else {
            send(event)
        }
    }

    private fun send(event: StockMovementEvent) {
        kafkaTemplate.send(topic, "${event.skuId}:${event.locationId}", event)
            .whenComplete { _, ex ->
                // 커밋 후 비동기 발행이라 예외를 삼키고 로그만 — 원장 기록 자체는 이미 확정됐다.
                if (ex != null) log.error(ex) { "stock.movement 발행 실패: movementId=${event.movementId}" }
            }
    }
}
