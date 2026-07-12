package com.gijun.wms.master.infrastructure.adapter.out.persistence.item

import com.gijun.wms.master.application.port.out.persistence.item.ItemCommandPersistencePort
import com.gijun.wms.master.application.port.out.persistence.item.ItemQueryPersistencePort
import com.gijun.wms.master.domain.enums.ItemStatus
import com.gijun.wms.master.domain.item.ProductModel
import com.gijun.wms.master.domain.item.SkuModel
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

/**
 * 상품/SKU 퍼시스턴스 어댑터 — command/query 포트 구현. 엔티티↔모델 변환 외 로직 없음.
 */
@Repository
class ItemPersistenceAdapter(
    private val productJpaRepository: ProductJpaRepository,
    private val skuJpaRepository: SkuJpaRepository,
) : ItemCommandPersistencePort, ItemQueryPersistencePort {

    override fun saveProduct(model: ProductModel): ProductModel =
        productJpaRepository.save(ProductJpaEntity.from(model)).toModel()

    override fun findProductById(id: Long): ProductModel? =
        productJpaRepository.findByIdOrNull(id)?.toModel()

    override fun saveSku(model: SkuModel): SkuModel =
        skuJpaRepository.save(SkuJpaEntity.from(model)).toModel()

    override fun findSkuById(id: Long): SkuModel? =
        skuJpaRepository.findByIdOrNull(id)?.toModel()

    override fun existsProductByCode(code: String): Boolean =
        productJpaRepository.existsByCode(code)

    override fun findProducts(status: ItemStatus?): List<ProductModel> =
        (status?.let { productJpaRepository.findByStatus(it) } ?: productJpaRepository.findAll())
            .map { it.toModel() }

    override fun findSkusByProductId(productId: Long): List<SkuModel> =
        skuJpaRepository.findByProductId(productId).map { it.toModel() }

    override fun existsSkuByCode(skuCode: String): Boolean =
        skuJpaRepository.existsBySkuCode(skuCode)

    override fun existsSkuByBarcode(barcode: String): Boolean =
        skuJpaRepository.existsByBarcode(barcode)

    override fun findSkuByCode(skuCode: String): SkuModel? =
        skuJpaRepository.findBySkuCode(skuCode)?.toModel()

    override fun findSkuByBarcode(barcode: String): SkuModel? =
        skuJpaRepository.findByBarcode(barcode)?.toModel()
}
