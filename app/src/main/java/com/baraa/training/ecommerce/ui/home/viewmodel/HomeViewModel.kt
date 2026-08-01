package com.baraa.training.ecommerce.ui.home.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.baraa.training.ecommerce.data.models.Resource
import com.baraa.training.ecommerce.data.models.products.ProductModel
import com.baraa.training.ecommerce.data.models.products.ProductSaleType
import com.baraa.training.ecommerce.data.models.user.CountryData
import com.baraa.training.ecommerce.data.repository.categories.CategoriesRepository
import com.baraa.training.ecommerce.data.repository.home.SalesAdsRepository
import com.baraa.training.ecommerce.data.repository.product.ProductsRepository
import com.baraa.training.ecommerce.data.repository.special_sections.SpecialSectionsRepository
import com.baraa.training.ecommerce.data.repository.user.UserPreferenceRepository
import com.baraa.training.ecommerce.domain.models.toProductUIModel
import com.baraa.training.ecommerce.domain.models.toSpecialSectionUIModel
import com.baraa.training.ecommerce.ui.products.model.ProductUIModel
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    salesAdsRepository: SalesAdsRepository,
    categoriesRepository: CategoriesRepository,
    private val productsRepository: ProductsRepository,
    userPreferenceRepository: UserPreferenceRepository,
    specialSectionsRepository: SpecialSectionsRepository
) : ViewModel() {

    val salesAdsState = salesAdsRepository.getSalesAds().stateIn(
        viewModelScope + IO, started = SharingStarted.Eagerly, initialValue = Resource.Loading()
    )

    val categoriesState = categoriesRepository.getCategories().stateIn(
        viewModelScope + IO, started = SharingStarted.Eagerly, initialValue = Resource.Loading()
    )

    private val countryState = userPreferenceRepository.getUserCountry().stateIn(
        scope = viewModelScope + IO,
        started = SharingStarted.Eagerly,
        initialValue = CountryData.getDefaultInstance()
    )

    val flashSaleState = getProductsSales(ProductSaleType.FLASH_SALE)

    val megaSaleState = getProductsSales(ProductSaleType.MEGA_SALE)

    val isEmptyFlashSale: LiveData<Boolean> = flashSaleState.map { it.isEmpty() }.asLiveData()

    val isEmptyMegaSale: LiveData<Boolean> = megaSaleState.map { it.isEmpty() }.asLiveData()

    @OptIn(ExperimentalCoroutinesApi::class)
    val recommendedSectionDataState = specialSectionsRepository.recommendProductsSection().stateIn(
        viewModelScope + IO, started = SharingStarted.Eagerly, initialValue = null
    ).mapLatest { it?.toSpecialSectionUIModel() }

    val isRecommendedSection = recommendedSectionDataState.map { it == null }.asLiveData()

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getProductsSales(productSaleType: ProductSaleType): StateFlow<List<ProductUIModel>> =
        countryState.mapLatest {
            Log.d(TAG, "CountryId for flash sale: ${it.id}")
            productsRepository.getSaleProducts(it.id ?: "0", productSaleType.type, 10)
        }.mapLatest { it.first().map { product -> getProductModel(product) } }.stateIn(
            viewModelScope + IO, started = SharingStarted.Eagerly, initialValue = emptyList()
        )

    private fun getProductModel(product: ProductModel): ProductUIModel {
        val productUIModel = product.toProductUIModel().copy(
            currencySymbol = countryState.value?.currencySymbol ?: ""
        )
        return productUIModel
    }

    fun stopTimer() {
        salesAdsState.value.data?.forEach { it.stopCountdown() }
    }

    fun startTimer() {
        salesAdsState.value.data?.forEach { it.startCountdown() }
    }

    private val _allProductsState: MutableStateFlow<List<ProductUIModel>> =
        MutableStateFlow(emptyList())
    val allProductsState = _allProductsState.asStateFlow()
    private val isLoadingAllProducts = MutableStateFlow(false)
    private val isFinishedLoadAllProducts = MutableStateFlow(false)
    private var lastDocumentSnapshot: DocumentSnapshot? = null

    fun getNextProducts() = viewModelScope.launch(IO) {
        if (isFinishedLoadAllProducts.value) return@launch
        if (isLoadingAllProducts.value) return@launch
        isLoadingAllProducts.emit(true)

        Log.d(TAG, "HomeViewModel - countryID: ${countryState.first().id}") // arrived correctly
        productsRepository.getAllProductsPaging(countryState.first().id, 6, lastDocumentSnapshot)
            .collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        isLoadingAllProducts.emit(false)
                        resource.data?.let { docs ->
                            if (docs.isEmpty) {
                                isFinishedLoadAllProducts.emit(true)
                                if (_allProductsState.value.isEmpty()) {
                                    _allProductsState.emit(getFallbackProducts())
                                }
                                return@collectLatest
                            } else {
                                lastDocumentSnapshot = docs.documents.lastOrNull()
                                val lstProducts = docs.toObjects(ProductModel::class.java)
                                    .map { getProductModel(it) }
                                _allProductsState.emit(_allProductsState.value + lstProducts)
                            }
                        }
                    }

                    is Resource.Error -> {
                        isLoadingAllProducts.emit(false)
                        if (_allProductsState.value.isEmpty()) {
                            _allProductsState.emit(getFallbackProducts())
                        }
                        Log.d(TAG, "getNextProducts: ${resource.exception?.message}")
                    }

                    is Resource.Loading -> {
                        isLoadingAllProducts.emit(true)
                    }
                }
            }
    }

    private fun getFallbackProducts(): List<ProductUIModel> {
        return listOf(
            ProductUIModel(
                id = "p1",
                name = "Nike Air Max 270 React",
                description = "The Nike Air Max 270 React combines a full-length React foam midsole with a 270 Max Air unit for unrivaled comfort.",
                images = listOf("https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800"),
                price = 299,
                rate = 4.5f,
                salePercentage = 24
            ),
            ProductUIModel(
                id = "p2",
                name = "QUAPRI Leather Backpack",
                description = "Premium genuine leather backpack crafted for daily commuters.",
                images = listOf("https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800"),
                price = 199,
                rate = 4.8f,
                salePercentage = 30
            ),
            ProductUIModel(
                id = "p3",
                name = "Air Jordan 1 Retro High",
                description = "Iconic basketball sneaker featuring genuine leather upper.",
                images = listOf("https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=800"),
                price = 349,
                rate = 4.9f,
                salePercentage = 50
            ),
            ProductUIModel(
                id = "p4",
                name = "Slim Fit Business Shirt",
                description = "100% breathable cotton slim fit shirt.",
                images = listOf("https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=800"),
                price = 99,
                rate = 4.3f,
                salePercentage = 40
            )
        )
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}