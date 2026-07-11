package com.stockscan.app.data.api

data class ItemDto(
    val barcode: String,
    val name: String,
    val quantity: Int,
    val updatedAt: String,
)

data class MovementDto(
    val id: Long,
    val barcode: String,
    val type: String,
    val quantity: Int,
    val quantityAfter: Int,
    val memo: String?,
    val createdAt: String,
)

data class CreateItemRequest(
    val barcode: String,
    val name: String,
    val quantity: Int,
)

data class MovementRequest(
    val quantity: Int,
    val memo: String? = null,
)
