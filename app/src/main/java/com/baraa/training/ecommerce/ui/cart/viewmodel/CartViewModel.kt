package com.baraa.training.ecommerce.ui.cart.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baraa.training.ecommerce.data.models.cart.CartItemModel
import com.baraa.training.ecommerce.data.repository.cart.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartUiState(
    val items: List<CartItemModel> = emptyList(),
    val subtotal: Int = 0,
    val shippingFee: Int = 10,
    val importCharges: Int = 5,
    val totalPrice: Int = 0
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    val cartUiState: StateFlow<CartUiState> = cartRepository.getCartItems()
        .map { items ->
            val subtotal = items.sumOf { it.price * it.quantity }
            val shipping = if (items.isNotEmpty()) 10 else 0
            val importFee = if (items.isNotEmpty()) 5 else 0
            val total = if (items.isNotEmpty()) subtotal + shipping + importFee else 0

            CartUiState(
                items = items,
                subtotal = subtotal,
                shippingFee = shipping,
                importCharges = importFee,
                totalPrice = total
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = CartUiState()
        )

    fun updateQuantity(cartItemId: String, newQuantity: Int) {
        viewModelScope.launch {
            cartRepository.updateQuantity(cartItemId, newQuantity)
        }
    }

    fun removeItem(cartItemId: String) {
        viewModelScope.launch {
            cartRepository.removeFromCart(cartItemId)
        }
    }

    fun checkout() {
        viewModelScope.launch {
            cartRepository.clearCart()
        }
    }
}
