package com.gijun.wms.master.application.handler.query

import com.gijun.wms.master.application.dto.query.GetProductQuery
import com.gijun.wms.master.application.dto.query.ListProductsQuery
import com.gijun.wms.master.application.dto.query.LookupSkuQuery
import com.gijun.wms.master.application.dto.result.ProductDetailResult
import com.gijun.wms.master.application.dto.result.ProductResult
import com.gijun.wms.master.application.dto.result.SkuResult
import com.gijun.wms.master.application.port.`in`.query.GetProductQueryUseCase
import com.gijun.wms.master.application.port.`in`.query.ListProductsQueryUseCase
import com.gijun.wms.master.application.port.`in`.query.LookupSkuQueryUseCase
import com.gijun.wms.master.application.port.out.persistence.item.ItemQueryPersistencePort
import com.gijun.wms.master.domain.item.exception.ItemException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ItemQueryHandler(
    private val itemQueryPersistencePort: ItemQueryPersistencePort,
) : GetProductQueryUseCase, ListProductsQueryUseCase, LookupSkuQueryUseCase {

    @Transactional(readOnly = true)
    override fun getProduct(query: GetProductQuery): ProductDetailResult {
        val product = itemQueryPersistencePort.findProductById(query.productId)
            ?: throw ItemException.ProductNotFoundException(query.productId)
        val skus = itemQueryPersistencePort.findSkusByProductId(query.productId)
        return ProductDetailResult.from(product, skus)
    }

    @Transactional(readOnly = true)
    override fun listProducts(query: ListProductsQuery): List<ProductResult> =
        itemQueryPersistencePort.findProducts(query.status).map(ProductResult::from)

    /** 바코드 스캔/코드 검색 — skuCode 또는 barcode 중 정확히 하나로 찾는다. */
    @Transactional(readOnly = true)
    override fun lookupSku(query: LookupSkuQuery): SkuResult {
        val sku = when {
            query.skuCode != null && query.barcode == null ->
                itemQueryPersistencePort.findSkuByCode(query.skuCode)
                    ?: throw ItemException.SkuNotFoundException("skuCode=${query.skuCode}")
            query.barcode != null && query.skuCode == null ->
                itemQueryPersistencePort.findSkuByBarcode(query.barcode)
                    ?: throw ItemException.SkuNotFoundException("barcode=${query.barcode}")
            else -> throw ItemException.InvalidSkuLookupException()
        }
        return SkuResult.from(sku)
    }
}
