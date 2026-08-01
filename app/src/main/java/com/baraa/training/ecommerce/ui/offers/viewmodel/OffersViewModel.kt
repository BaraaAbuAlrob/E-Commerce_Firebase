package com.baraa.training.ecommerce.ui.offers.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baraa.training.ecommerce.data.repository.product.ProductsRepository
import com.baraa.training.ecommerce.domain.models.toProductUIModel
import com.baraa.training.ecommerce.ui.products.model.ProductUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OffersViewModel @Inject constructor(
    private val productsRepository: ProductsRepository
) : ViewModel() {

    private val _flashSaleState = MutableStateFlow<List<ProductUIModel>>(emptyList())
    val flashSaleState: StateFlow<List<ProductUIModel>> = _flashSaleState

    private val _megaSaleState = MutableStateFlow<List<ProductUIModel>>(emptyList())
    val megaSaleState: StateFlow<List<ProductUIModel>> = _megaSaleState

    init {
        loadOffers()
    }

    private fun loadOffers() {
        viewModelScope.launch {
            productsRepository.getSaleProducts("", "flash_sale", 10).collectLatest { list ->
                _flashSaleState.value = list.map { it.toProductUIModel() }
            }
        }

        viewModelScope.launch {
            productsRepository.getSaleProducts("", "mega_sale", 10).collectLatest { list ->
                _megaSaleState.value = list.map { it.toProductUIModel() }
            }
        }
    }
}
