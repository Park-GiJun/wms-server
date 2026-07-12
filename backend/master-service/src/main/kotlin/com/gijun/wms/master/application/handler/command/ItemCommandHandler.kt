package com.gijun.wms.master.application.handler.command

import com.gijun.wms.master.application.dto.command.AddSkuCommand
import com.gijun.wms.master.application.dto.command.ChangeProductStatusCommand
import com.gijun.wms.master.application.dto.command.ChangeSkuStatusCommand
import com.gijun.wms.master.application.dto.command.CreateProductCommand
import com.gijun.wms.master.application.dto.command.NewSkuData
import com.gijun.wms.master.application.dto.command.UpdateProductCommand
import com.gijun.wms.master.application.dto.command.UpdateSkuCommand
import com.gijun.wms.master.application.dto.result.ProductDetailResult
import com.gijun.wms.master.application.dto.result.ProductResult
import com.gijun.wms.master.application.dto.result.SkuResult
import com.gijun.wms.master.application.port.`in`.command.AddSkuCommandUseCase
import com.gijun.wms.master.application.port.`in`.command.ChangeProductStatusCommandUseCase
import com.gijun.wms.master.application.port.`in`.command.ChangeSkuStatusCommandUseCase
import com.gijun.wms.master.application.port.`in`.command.CreateProductCommandUseCase
import com.gijun.wms.master.application.port.`in`.command.UpdateProductCommandUseCase
import com.gijun.wms.master.application.port.`in`.command.UpdateSkuCommandUseCase
import com.gijun.wms.master.application.port.out.persistence.item.ItemCommandPersistencePort
import com.gijun.wms.master.application.port.out.persistence.item.ItemQueryPersistencePort
import com.gijun.wms.master.domain.enums.ItemStatus
import com.gijun.wms.master.domain.item.ProductModel
import com.gijun.wms.master.domain.item.SkuModel
import com.gijun.wms.master.domain.item.exception.ItemException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ItemCommandHandler(
    private val itemCommandPersistencePort: ItemCommandPersistencePort,
    private val itemQueryPersistencePort: ItemQueryPersistencePort,
) : CreateProductCommandUseCase, UpdateProductCommandUseCase, ChangeProductStatusCommandUseCase,
    AddSkuCommandUseCase, UpdateSkuCommandUseCase, ChangeSkuStatusCommandUseCase {

    /** 상품 생성 — 재고는 SKU 단위이므로 SKU 최소 1개를 같은 트랜잭션에서 함께 만든다. */
    @Transactional
    override fun createProduct(command: CreateProductCommand): ProductDetailResult {
        if (command.skus.isEmpty()) {
            throw ItemException.EmptySkuException()
        }
        if (itemQueryPersistencePort.existsProductByCode(command.code)) {
            throw ItemException.DuplicateProductCodeException(command.code)
        }
        validateNoDuplicateInCommand(command.skus)
        command.skus.forEach { validateSkuUniqueness(it.skuCode, it.barcode) }

        val product = itemCommandPersistencePort.saveProduct(
            ProductModel.create(
                code = command.code,
                name = command.name,
                largeCategory = command.largeCategory,
                mediumCategory = command.mediumCategory,
                smallCategory = command.smallCategory,
            ),
        )
        val productId = requireNotNull(product.id)
        val skus = command.skus.map {
            itemCommandPersistencePort.saveSku(
                SkuModel.create(productId, it.skuCode, it.barcode, it.options, it.unit),
            )
        }
        return ProductDetailResult.from(product, skus)
    }

    @Transactional
    override fun updateProduct(command: UpdateProductCommand): ProductResult {
        val product = loadProduct(command.productId)
        return ProductResult.from(
            itemCommandPersistencePort.saveProduct(
                product.update(
                    name = command.name,
                    largeCategory = command.largeCategory,
                    mediumCategory = command.mediumCategory,
                    smallCategory = command.smallCategory,
                ),
            ),
        )
    }

    /** 상태 전이 검증(같은 상태 거부)은 도메인(changeStatus)이 한다. */
    @Transactional
    override fun changeProductStatus(command: ChangeProductStatusCommand): ProductResult {
        val product = loadProduct(command.productId)
        return ProductResult.from(
            itemCommandPersistencePort.saveProduct(product.changeStatus(command.status)),
        )
    }

    /** SKU 추가 — ACTIVE 상품에만 가능. */
    @Transactional
    override fun addSku(command: AddSkuCommand): SkuResult {
        val product = loadProduct(command.productId)
        if (product.status != ItemStatus.ACTIVE) {
            throw ItemException.ProductNotActiveException(command.productId)
        }
        validateSkuUniqueness(command.sku.skuCode, command.sku.barcode)
        return SkuResult.from(
            itemCommandPersistencePort.saveSku(
                SkuModel.create(
                    productId = command.productId,
                    skuCode = command.sku.skuCode,
                    barcode = command.sku.barcode,
                    options = command.sku.options,
                    unit = command.sku.unit,
                ),
            ),
        )
    }

    @Transactional
    override fun updateSku(command: UpdateSkuCommand): SkuResult {
        val sku = loadSku(command.skuId)
        if (command.barcode != null && command.barcode != sku.barcode &&
            itemQueryPersistencePort.existsSkuByBarcode(command.barcode)
        ) {
            throw ItemException.DuplicateBarcodeException(command.barcode)
        }
        return SkuResult.from(
            itemCommandPersistencePort.saveSku(
                sku.update(barcode = command.barcode, options = command.options, unit = command.unit),
            ),
        )
    }

    @Transactional
    override fun changeSkuStatus(command: ChangeSkuStatusCommand): SkuResult {
        val sku = loadSku(command.skuId)
        return SkuResult.from(
            itemCommandPersistencePort.saveSku(sku.changeStatus(command.status)),
        )
    }

    private fun loadProduct(productId: Long): ProductModel =
        itemCommandPersistencePort.findProductById(productId)
            ?: throw ItemException.ProductNotFoundException(productId)

    private fun loadSku(skuId: Long): SkuModel =
        itemCommandPersistencePort.findSkuById(skuId)
            ?: throw ItemException.SkuNotFoundException("id=$skuId")

    /** 한 커맨드 안에서의 skuCode/바코드 중복도 저장 전에 걸러낸다. */
    private fun validateNoDuplicateInCommand(skus: List<NewSkuData>) {
        skus.groupingBy { it.skuCode }.eachCount().filterValues { it > 1 }.keys.firstOrNull()
            ?.let { throw ItemException.DuplicateSkuCodeException(it) }
        skus.mapNotNull { it.barcode }.groupingBy { it }.eachCount().filterValues { it > 1 }.keys.firstOrNull()
            ?.let { throw ItemException.DuplicateBarcodeException(it) }
    }

    private fun validateSkuUniqueness(skuCode: String, barcode: String?) {
        if (itemQueryPersistencePort.existsSkuByCode(skuCode)) {
            throw ItemException.DuplicateSkuCodeException(skuCode)
        }
        if (barcode != null && itemQueryPersistencePort.existsSkuByBarcode(barcode)) {
            throw ItemException.DuplicateBarcodeException(barcode)
        }
    }
}
