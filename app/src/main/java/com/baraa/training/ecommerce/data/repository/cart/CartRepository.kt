package com.baraa.training.ecommerce.data.repository.cart

import com.baraa.training.ecommerce.data.models.cart.CartItemModel
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartItems(): Flow<List<CartItemModel>>
    suspend fun addToCart(item: CartItemModel)
    suspend fun updateQuantity(cartItemId: String, newQuantity: Int)
    suspend fun removeFromCart(cartItemId: String)
    suspend fun clearCart()
}
