package com.gijun.wms.master.domain.stock.exception

import com.gijun.wms.shared.exception.ErrorCode
import com.gijun.wms.shared.exception.WmsException

/**
 * 재고원장 도메인 예외. 서브클래스가 [ErrorCode] 를 고정하고, 호출부는 컨텍스트(수량 등)만 넘긴다.
 */
sealed class StockException(
    code: ErrorCode,
    message: String = code.defaultMessage,
) : WmsException(code, message) {

    /** 가용 재고 부족(음수재고/오버피킹 차단) — 422. */
    class InsufficientStockException(skuId: Long, locationId: Long, requested: Long, available: Long) :
        StockException(
            ErrorCode.INSUFFICIENT_STOCK,
            "가용 재고가 부족합니다: sku=$skuId, location=$locationId, 요청=$requested, 가용=$available",
        )

    /** 이동 수량 규칙 위반(0 또는 잘못된 부호) — 400. */
    class InvalidQuantityException(message: String) :
        StockException(ErrorCode.INVALID_INPUT, message)

    /** 출발지와 도착지가 같은 이동 — 400. */
    class SameLocationTransferException(locationId: Long) :
        StockException(ErrorCode.INVALID_INPUT, "출발지와 도착지가 같습니다: location=$locationId")

    /** 비활성(INACTIVE) 대상으로의 재고 유입 시도 — 409. 반출·조정은 상태 불문 허용된다. */
    class InactiveTargetException(target: String) :
        StockException(ErrorCode.CONFLICT, "비활성 대상으로는 재고를 유입할 수 없습니다: $target")
}
