package com.stockscan.inventory

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class InventoryService(
    private val items: StockItemRepository,
    private val movements: StockMovementRepository,
) {
    @Transactional(readOnly = true)
    fun listItems(): List<StockItem> = items.findAll(Sort.by("name"))

    @Transactional(readOnly = true)
    fun getByBarcode(barcode: String): StockItem = items.findByBarcode(barcode) ?: throw ItemNotFoundException(barcode)

    fun register(
        barcode: String,
        name: String,
        quantity: Int,
    ): StockItem {
        if (items.existsByBarcode(barcode)) {
            throw DuplicateBarcodeException(barcode)
        }
        val saved = items.save(StockItem(barcode, name, quantity))
        if (quantity > 0) {
            movements.save(StockMovement(barcode, MovementType.INBOUND, quantity, quantity, "신규 등록"))
        }
        return saved
    }

    fun receive(
        barcode: String,
        amount: Int,
        memo: String?,
    ): StockItem {
        val item = getByBarcode(barcode)
        item.receive(amount)
        movements.save(StockMovement(barcode, MovementType.INBOUND, amount, item.quantity, memo))
        return item
    }

    fun release(
        barcode: String,
        amount: Int,
        memo: String?,
    ): StockItem {
        val item = getByBarcode(barcode)
        item.release(amount)
        movements.save(StockMovement(barcode, MovementType.OUTBOUND, amount, item.quantity, memo))
        return item
    }

    @Transactional(readOnly = true)
    fun history(barcode: String?): List<StockMovement> =
        if (barcode.isNullOrBlank()) {
            movements.findAllByOrderByCreatedAtDesc()
        } else {
            movements.findByBarcodeOrderByCreatedAtDesc(barcode)
        }
}
