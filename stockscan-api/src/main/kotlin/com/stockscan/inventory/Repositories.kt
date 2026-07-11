package com.stockscan.inventory

import org.springframework.data.jpa.repository.JpaRepository

interface StockItemRepository : JpaRepository<StockItem, Long> {
    fun findByBarcode(barcode: String): StockItem?

    fun existsByBarcode(barcode: String): Boolean
}

interface StockMovementRepository : JpaRepository<StockMovement, Long> {
    fun findByBarcodeOrderByCreatedAtDesc(barcode: String): List<StockMovement>

    fun findAllByOrderByCreatedAtDesc(): List<StockMovement>
}
