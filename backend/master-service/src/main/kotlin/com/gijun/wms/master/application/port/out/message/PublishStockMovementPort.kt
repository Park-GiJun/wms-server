package com.gijun.wms.master.application.port.out.message

import com.gijun.wms.shared.event.StockMovementEvent

/**
 * 척추 이벤트 발행 포트. 구현체는 **트랜잭션 커밋 이후에만** 실제 발행해야 한다 —
 * 롤백된 이동이 컨슈머에 전파되면 원장과 투영이 어긋난다.
 */
interface PublishStockMovementPort {
    fun publish(event: StockMovementEvent)
}
