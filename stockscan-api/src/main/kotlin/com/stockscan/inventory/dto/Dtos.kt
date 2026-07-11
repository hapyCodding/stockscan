package com.stockscan.inventory.dto

import com.stockscan.inventory.MovementType
import com.stockscan.inventory.StockItem
import com.stockscan.inventory.StockMovement
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import java.time.Instant

data class CreateItemRequest(
    @field:NotBlank val barcode: String,
    @field:NotBlank val name: String,
    @field:PositiveOrZero val quantity: Int = 0,
)

data class MovementRequest(
    @field:Positive val quantity: Int,
    val memo: String? = null,
)

data class ItemResponse(
    val barcode: String,
    val name: String,
    val quantity: Int,
    val updatedAt: Instant,
) {
    companion object {
        fun from(item: StockItem) = ItemResponse(item.barcode, item.name, item.quantity, item.updatedAt)
    }
}

data class MovementResponse(
    val id: Long,
    val barcode: String,
    val type: MovementType,
    val quantity: Int,
    val quantityAfter: Int,
    val memo: String?,
    val createdAt: Instant,
) {
    companion object {
        fun from(movement: StockMovement) =
            MovementResponse(
                movement.id,
                movement.barcode,
                movement.type,
                movement.quantity,
                movement.quantityAfter,
                movement.memo,
                movement.createdAt,
            )
    }
}
