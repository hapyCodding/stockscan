package com.stockscan.inventory

import com.stockscan.inventory.dto.CreateItemRequest
import com.stockscan.inventory.dto.ItemResponse
import com.stockscan.inventory.dto.MovementRequest
import com.stockscan.inventory.dto.MovementResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class InventoryController(
    private val inventory: InventoryService,
) {
    @GetMapping("/items")
    fun listItems(): List<ItemResponse> = inventory.listItems().map(ItemResponse::from)

    @GetMapping("/items/{barcode}")
    fun getItem(
        @PathVariable barcode: String,
    ): ItemResponse = ItemResponse.from(inventory.getByBarcode(barcode))

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(
        @Valid @RequestBody request: CreateItemRequest,
    ): ItemResponse = ItemResponse.from(inventory.register(request.barcode, request.name, request.quantity))

    @PostMapping("/items/{barcode}/inbound")
    fun receive(
        @PathVariable barcode: String,
        @Valid @RequestBody request: MovementRequest,
    ): ItemResponse = ItemResponse.from(inventory.receive(barcode, request.quantity, request.memo))

    @PostMapping("/items/{barcode}/outbound")
    fun release(
        @PathVariable barcode: String,
        @Valid @RequestBody request: MovementRequest,
    ): ItemResponse = ItemResponse.from(inventory.release(barcode, request.quantity, request.memo))

    @GetMapping("/movements")
    fun history(
        @RequestParam(required = false) barcode: String?,
    ): List<MovementResponse> = inventory.history(barcode).map(MovementResponse::from)
}
