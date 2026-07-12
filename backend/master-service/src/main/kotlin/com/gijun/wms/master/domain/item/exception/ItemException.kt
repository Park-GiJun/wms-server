package com.gijun.wms.master.domain.item.exception

import com.gijun.wms.master.domain.enums.ItemStatus
import com.gijun.wms.shared.exception.ErrorCode
import com.gijun.wms.shared.exception.WmsException

/**
 * 상품/SKU 도메인 예외. 서브클래스가 [ErrorCode] 를 고정하고, 호출부는 컨텍스트(코드 등)만 넘긴다.
 */
sealed class ItemException(
    code: ErrorCode,
    message: String = code.defaultMessage,
) : WmsException(code, message) {

    /** 상품 코드 중복 — 409. */
    class DuplicateProductCodeException(code: String) :
        ItemException(ErrorCode.CONFLICT, "이미 존재하는 상품 코드입니다: $code")

    /** SKU 코드 중복 — 409. */
    class DuplicateSkuCodeException(skuCode: String) :
        ItemException(ErrorCode.CONFLICT, "이미 존재하는 SKU 코드입니다: $skuCode")

    /** 바코드 중복 — 409. */
    class DuplicateBarcodeException(barcode: String) :
        ItemException(ErrorCode.CONFLICT, "이미 존재하는 바코드입니다: $barcode")

    /** 대상 상품 없음 — 404. */
    class ProductNotFoundException(productId: Long) :
        ItemException(ErrorCode.NOT_FOUND, "상품을 찾을 수 없습니다: $productId")

    /** 대상 SKU 없음 — 404. */
    class SkuNotFoundException(detail: String) :
        ItemException(ErrorCode.NOT_FOUND, "SKU 를 찾을 수 없습니다: $detail")

    /** 이미 목표 상태 — 409 (중복 요청 감지). */
    class AlreadyInStatusException(target: ItemStatus) :
        ItemException(ErrorCode.CONFLICT, "이미 $target 상태입니다.")

    /** 비활성 상품에 SKU 추가 시도 — 409. */
    class ProductNotActiveException(productId: Long) :
        ItemException(ErrorCode.CONFLICT, "비활성 상품에는 SKU 를 추가할 수 없습니다: $productId")

    /** 상품 생성에 SKU 가 하나도 없음 — 400. 재고는 SKU 단위라 상품은 최소 1 SKU 를 가져야 한다. */
    class EmptySkuException :
        ItemException(ErrorCode.INVALID_INPUT, "상품에는 최소 1개의 SKU 가 필요합니다.")

    /** SKU 조회 시 skuCode/barcode 중 정확히 하나만 허용 — 400. */
    class InvalidSkuLookupException :
        ItemException(ErrorCode.INVALID_INPUT, "skuCode 또는 barcode 중 정확히 하나를 지정해야 합니다.")
}
