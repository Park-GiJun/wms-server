package com.gijun.wms.master.infrastructure.adapter.out.persistence.stock

import com.gijun.wms.master.application.port.out.persistence.stock.StockCommandPersistencePort
import com.gijun.wms.master.domain.stock.StockApplication
import com.gijun.wms.master.domain.stock.StockBalanceModel
import com.gijun.wms.master.domain.stock.StockMovementModel
import org.springframework.stereotype.Repository

/**
 * 재고원장 퍼시스턴스 어댑터 — 락 획득과 기록만 담당, 원장 규칙은 도메인이 집행.
 */
@Repository
class StockPersistenceAdapter(
    private val stockMovementJpaRepository: StockMovementJpaRepository,
    private val stockBalanceJpaRepository: StockBalanceJpaRepository,
) : StockCommandPersistencePort {

    override fun ensureAndLock(skuId: Long, locationId: Long): StockBalanceModel {
        stockBalanceJpaRepository.insertIfAbsent(skuId, locationId)
        val entity = checkNotNull(stockBalanceJpaRepository.findForUpdate(StockBalanceId(skuId, locationId))) {
            "insertIfAbsent 이후에는 잔고 행이 반드시 존재해야 한다: sku=$skuId, location=$locationId"
        }
        return entity.toModel()
    }

    override fun persist(application: StockApplication): StockMovementModel {
        stockBalanceJpaRepository.save(StockBalanceJpaEntity.from(application.balance))
        return stockMovementJpaRepository.save(StockMovementJpaEntity.from(application.movement)).toModel()
    }
}
