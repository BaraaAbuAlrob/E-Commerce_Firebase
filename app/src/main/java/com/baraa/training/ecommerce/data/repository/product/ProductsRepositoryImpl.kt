package com.baraa.training.ecommerce.data.repository.product

import com.baraa.training.ecommerce.data.models.products.ProductModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProductsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ProductsRepository {

    override fun getCategoryProducts(categoryID: String, pageLimit: Int): Flow<List<ProductModel>> =
        flow {
            val products =
                firestore.collection("products").whereArrayContains("categories_ids", categoryID)
                    .limit(pageLimit.toLong()).get().await().toObjects(ProductModel::class.java)

            emit(products)
        }

    override fun getSaleProducts(
        countryID: String, saleType: String, pageLimit: Int
    ): Flow<List<ProductModel>> = flow {
        val products = firestore.collection("products")
            .whereEqualTo("countries.Wo8dMRJOte19tZIa3JVO.sale_type", saleType)
            .limit(pageLimit.toLong()).get().await().toObjects(ProductModel::class.java)

        emit(products)
    }
}