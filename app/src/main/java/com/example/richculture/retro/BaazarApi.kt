package com.example.richculture.retro

import com.example.richculture.Data.*
import retrofit2.Response
import retrofit2.http.*

interface BazaarApi {
    @GET("item/")
    suspend fun getAllItems(): Response<List<Product>>

    @GET("item/category/{categoryName}")
    suspend fun getItemsByCategory(@Path("categoryName") categoryName: String): Response<List<Product>>

    @GET("item/{id}")
    suspend fun getItemById(@Path("id") itemId: String): Response<Product>

    @POST("cart/add")
    suspend fun addToCart(
        @Header("Authorization") token: String,
        @Body request: AddToCartRequest
    ): Response<CartResponse>

    @GET("cart")
    suspend fun getCart(@Header("Authorization") token: String): Response<CartResponse>

    @PUT("cart/update/{itemId}")
    suspend fun updateCartItemQuantity(
        @Header("Authorization") token: String,
        @Path("itemId") itemId: String,
        @Body request: UpdateQuantityRequest
    ): Response<CartResponse>

    @DELETE("cart/remove/{itemId}")
    suspend fun removeCartItem(
        @Header("Authorization") token: String,
        @Path("itemId") itemId: String
    ): Response<CartResponse>
}

