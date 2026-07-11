package com.stockscan.app.data

import com.stockscan.app.data.api.CreateItemRequest
import com.stockscan.app.data.api.ItemDto
import com.stockscan.app.data.api.MovementRequest
import com.stockscan.app.data.api.StockApi
import retrofit2.HttpException

class InventoryRepository(
    private val api: StockApi,
) {
    suspend fun items(): List<ItemDto> = api.items()

    suspend fun find(barcode: String): ItemDto? =
        try {
            api.item(barcode)
        } catch (e: HttpException) {
            if (e.code() == 404) null else throw e
        }

    suspend fun register(
        barcode: String,
        name: String,
        quantity: Int,
    ): ItemDto = api.register(CreateItemRequest(barcode, name, quantity))

    suspend fun receive(
        barcode: String,
        quantity: Int,
    ): ItemDto = api.receive(barcode, MovementRequest(quantity))

    suspend fun release(
        barcode: String,
        quantity: Int,
    ): ItemDto = api.release(barcode, MovementRequest(quantity))
}
