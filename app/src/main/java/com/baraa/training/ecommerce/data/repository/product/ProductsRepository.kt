package com.baraa.training.ecommerce.data.repository.product

import com.baraa.training.ecommerce.data.models.products.ProductModel
import kotlinx.coroutines.flow.Flow

interface ProductsRepository {
    fun getCategoryProducts(categoryID: String, pageLimit: Int): Flow<List<ProductModel>>

    fun getSaleProducts(countryID: String, saleType: String, pageLimit: Int): Flow<List<ProductModel>>
}