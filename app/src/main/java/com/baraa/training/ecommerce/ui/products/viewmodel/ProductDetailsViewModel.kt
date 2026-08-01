package com.baraa.training.ecommerce.ui.products.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baraa.training.ecommerce.data.models.cart.CartItemModel
import com.baraa.training.ecommerce.data.repository.cart.CartRepository
import com.baraa.training.ecommerce.data.repository.product.ProductsRepository
import com.baraa.training.ecommerce.domain.models.toProductUIModel
import com.baraa.training.ecommerce.ui.products.ProductDetailsActivity.Companion.PRODUCT_ID_KEY
import com.baraa.training.ecommerce.ui.products.ProductDetailsActivity.Companion.PRODUCT_UI_MODEL_EXTRA
import com.baraa.training.ecommerce.ui.products.model.ProductUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val productsRepository: ProductsRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val initialProduct: ProductUIModel? = savedStateHandle.get<ProductUIModel>(PRODUCT_UI_MODEL_EXTRA)
    private val productId: String = savedStateHandle.get<String>(PRODUCT_ID_KEY) ?: initialProduct?.id ?: "p1"

    private val defaultProduct = initialProduct ?: ProductUIModel(
        id = productId,
        name = "Nike Air Max 270 React",
        description = "The Nike Air Max 270 React combines a full-length React foam midsole with a 270 Max Air unit for unrivaled comfort and striking visual style.",
        price = 299,
        rate = 4.5f,
        salePercentage = 24,
        images = listOf("https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800")
    )

    private val _productDetailsState = MutableStateFlow(defaultProduct)
    val productDetailsState = _productDetailsState.asStateFlow()

    init {
        listenToProductDetails()
    }

    private fun listenToProductDetails() = viewModelScope.launch(IO) {
        productsRepository.listenToProductDetails(productId).collectLatest { product ->
            _productDetailsState.value = product.toProductUIModel()
        }
    }

    fun addToCart(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val product = _productDetailsState.value
            val cartItem = CartItemModel(
                productId = product.id,
                productName = product.name,
                productImage = product.images.firstOrNull() ?: "",
                price = product.price,
                quantity = 1,
                selectedColor = product.colors.firstOrNull()?.color,
                selectedSize = product.sizes.firstOrNull()?.size
            )
            cartRepository.addToCart(cartItem)
            onSuccess()
        }
    }

    companion object {
        private const val TAG = "ProductDetailsViewModel"
    }
}