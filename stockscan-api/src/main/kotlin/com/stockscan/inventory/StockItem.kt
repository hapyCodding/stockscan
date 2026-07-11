package com.stockscan.inventory

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "stock_item")
class StockItem(
    @Column(nullable = false, unique = true, updatable = false)
    val barcode: String,
    @Column(nullable = false)
    var name: String,
    @Column(nullable = false)
    var quantity: Int = 0,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Column(nullable = false, updatable = false)
    val createdAt: Instant = Instant.now()

    @Column(nullable = false)
    var updatedAt: Instant = Instant.now()

    fun receive(amount: Int) {
        require(amount > 0) { "입고 수량은 1 이상이어야 합니다" }
        quantity += amount
        updatedAt = Instant.now()
    }

    fun release(amount: Int) {
        require(amount > 0) { "출고 수량은 1 이상이어야 합니다" }
        if (amount > quantity) {
            throw InsufficientStockException(barcode, quantity, amount)
        }
        quantity -= amount
        updatedAt = Instant.now()
    }
}
