package com.stockscan.app.data.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface StockApi {
    @GET("api/items")
    suspend fun items(): List<ItemDto>

    @GET("api/items/{barcode}")
    suspend fun item(
        @Path("barcode") barcode: String,
    ): ItemDto

    @POST("api/items")
    suspend fun register(
        @Body body: CreateItemRequest,
    ): ItemDto

    @POST("api/items/{barcode}/inbound")
    suspend fun receive(
        @Path("barcode") barcode: String,
        @Body body: MovementRequest,
    ): ItemDto

    @POST("api/items/{barcode}/outbound")
    suspend fun release(
        @Path("barcode") barcode: String,
        @Body body: MovementRequest,
    ): ItemDto

    @GET("api/movements")
    suspend fun movements(
        @Query("barcode") barcode: String?,
    ): List<MovementDto>
}
