package com.stockscan.inventory

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "stock_movement")
class StockMovement(
    @Column(nullable = false)
    val barcode: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: MovementType,
    @Column(nullable = false)
    val quantity: Int,
    @Column(nullable = false)
    val quantityAfter: Int,
    @Column
    val memo: String? = null,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Column(nullable = false, updatable = false)
    val createdAt: Instant = Instant.now()
}
