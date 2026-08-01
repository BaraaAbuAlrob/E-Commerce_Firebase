package com.baraa.training.ecommerce.ui.explore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baraa.training.ecommerce.data.models.Resource
import com.baraa.training.ecommerce.data.repository.categories.CategoriesRepository
import com.baraa.training.ecommerce.data.repository.product.ProductsRepository
import com.baraa.training.ecommerce.domain.models.toProductUIModel
import com.baraa.training.ecommerce.ui.home.model.CategoryUIModel
import com.baraa.training.ecommerce.ui.products.model.ProductUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val categoriesRepository: CategoriesRepository,
    private val productsRepository: ProductsRepository
) : ViewModel() {

    private val _categoriesState = MutableStateFlow<List<CategoryUIModel>>(emptyList())
    val categoriesState: StateFlow<List<CategoryUIModel>> = _categoriesState

    private val _productsState = MutableStateFlow<List<ProductUIModel>>(emptyList())
    val productsState: StateFlow<List<ProductUIModel>> = _productsState

    private var allProductsList = listOf<ProductUIModel>()

    init {
        loadCategories()
        loadProducts()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoriesRepository.getCategories().collectLatest { resource ->
                if (resource is Resource.Success && resource.data != null) {
                    _categoriesState.value = resource.data
                }
            }
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            productsRepository.getSaleProducts("", "", 20).collectLatest { list ->
                val uiList = list.map { it.toProductUIModel() }
                allProductsList = uiList
                _productsState.value = uiList
            }
        }
    }

    fun searchProducts(query: String) {
        if (query.isBlank()) {
            _productsState.value = allProductsList
        } else {
            _productsState.value = allProductsList.filter {
                it.name.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true)
            }
        }
    }

    fun filterByCategory(categoryId: String) {
        viewModelScope.launch {
            productsRepository.getCategoryProducts(categoryId, 20).collectLatest { list ->
                _productsState.value = list.map { it.toProductUIModel() }
            }
        }
    }
}
