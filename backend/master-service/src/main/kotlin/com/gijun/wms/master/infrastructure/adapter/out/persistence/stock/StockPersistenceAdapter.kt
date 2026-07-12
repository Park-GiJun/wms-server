package com.gijun.wms.master.infrastructure.adapter.out.persistence.stock

import com.gijun.wms.master.application.port.out.persistence.stock.StockCommandPersistencePort
import com.gijun.wms.master.application.port.out.persistence.stock.StockQueryPersistencePort
import com.gijun.wms.master.domain.stock.StockApplication
import com.gijun.wms.master.domain.stock.StockBalanceModel
import com.gijun.wms.master.domain.stock.StockMovementModel
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

/**
 * 재고원장 퍼시스턴스 어댑터 — command/query 포트 구현. 락 획득과 기록·조회만 담당, 원장 규칙은 도메인이 집행.
 */
@Repository
class StockPersistenceAdapter(
    private val stockMovementJpaRepository: StockMovementJpaRepository,
    private val stockBalanceJpaRepository: StockBalanceJpaRepository,
) : StockCommandPersistencePort, StockQueryPersistencePort {

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

    override fun findBalances(skuId: Long?, locationId: Long?): List<StockBalanceModel> {
        val entities = when {
            skuId != null && locationId != null ->
                listOfNotNull(stockBalanceJpaRepository.findByIdOrNull(StockBalanceId(skuId, locationId)))
            skuId != null -> stockBalanceJpaRepository.findByIdSkuId(skuId)
            locationId != null -> stockBalanceJpaRepository.findByIdLocationId(locationId)
            else -> stockBalanceJpaRepository.findAll()
        }
        return entities.map { it.toModel() }
    }

    override fun findMovements(skuId: Long?, locationId: Long?): List<StockMovementModel> {
        val entities = when {
            skuId != null && locationId != null ->
                stockMovementJpaRepository.findBySkuIdAndLocationIdOrderByIdDesc(skuId, locationId)
            skuId != null -> stockMovementJpaRepository.findBySkuIdOrderByIdDesc(skuId)
            locationId != null -> stockMovementJpaRepository.findByLocationIdOrderByIdDesc(locationId)
            // 무필터 전체 조회는 핸들러가 거부한다 — 여기 도달하면 호출 계약 위반.
            else -> error("원장 이력 조회는 skuId/locationId 중 하나 이상이 필요하다")
        }
        return entities.map { it.toModel() }
    }
}
