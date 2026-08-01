package com.baraa.training.ecommerce.data.repository.product

import android.util.Log
import com.baraa.training.ecommerce.data.models.Resource
import com.baraa.training.ecommerce.data.models.products.ProductColorModel
import com.baraa.training.ecommerce.data.models.products.ProductModel
import com.baraa.training.ecommerce.data.models.products.ProductSizeModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProductsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ProductsRepository {

    override fun getCategoryProducts(categoryID: String, pageLimit: Int): Flow<List<ProductModel>> =
        flow {
            val result = try {
                val products =
                    firestore.collection("products").whereArrayContains("categories_ids", categoryID)
                        .limit(pageLimit.toLong()).get().await().toObjects(ProductModel::class.java)

                if (products.isNotEmpty()) {
                    products
                } else {
                    getFallbackProducts().filter { it.categoriesIDs?.contains(categoryID) == true || categoryID.isEmpty() }
                }
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "getCategoryProducts error", e)
                getFallbackProducts()
            }
            emit(result)
        }

    override fun getSaleProducts(
        countryID: String, saleType: String, pageLimit: Int
    ): Flow<List<ProductModel>> = flow {
            val result = try {
                Log.d("ProductsRepositoryImpl", "getSaleProducts: $countryID, $saleType")
                val products = firestore.collection("products")
                    .whereEqualTo("sale_type", saleType)
                    .limit(pageLimit.toLong()).get().await().toObjects(ProductModel::class.java)

                if (products.isNotEmpty()) {
                    products
                } else {
                    getFallbackProducts().filter { it.saleType == saleType || saleType.isEmpty() }
                }
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "getSaleProducts error", e)
                getFallbackProducts().filter { it.saleType == saleType || saleType.isEmpty() }
            }
            emit(result)
        }

    override suspend fun getAllProductsPaging(
        countryID: String, pageLimit: Long, lastDocument: DocumentSnapshot?
    ) = flow<Resource<QuerySnapshot>> {
        try {
            emit(Resource.Loading())
            var firstQuery = firestore.collection("products").orderBy("price")

            if (lastDocument != null) {
                firstQuery = firstQuery.startAfter(lastDocument)
            }

            firstQuery = firstQuery.limit(pageLimit)

            val products = firstQuery.get().await()
            emit(Resource.Success(products))
        } catch (e: Exception) {
            Log.d(TAG, "getAllProductsPaging: ${e.message}")
            emit(Resource.Error(e))
        }
    }

    override fun listenToProductDetails(productID: String): Flow<ProductModel> {
        return callbackFlow {
            val listener = firestore.collection("products").document(productID)
                .addSnapshotListener { value, error ->
                    if (error != null || value == null || !value.exists()) {
                        Log.d(TAG, "listenToProductDetails fallback used")
                        val fallback = getFallbackProducts().find { it.id == productID } ?: getFallbackProducts().first()
                        trySend(fallback)
                        return@addSnapshotListener
                    }

                    val product = value.toObject(ProductModel::class.java)
                    if (product != null) {
                        trySend(product)
                    } else {
                        val fallback = getFallbackProducts().find { it.id == productID } ?: getFallbackProducts().first()
                        trySend(fallback)
                    }
                }

            awaitClose { listener.remove() }
        }
    }

    private fun getFallbackProducts(): List<ProductModel> {
        return listOf(
            ProductModel(
                id = "p1",
                name = "FS - Nike Air Max 270 React",
                description = "The Nike Air Max 270 React combines a full-length React foam midsole with a 270 Max Air unit for unrivaled comfort and striking visual style.",
                categoriesIDs = listOf("1", "5"),
                images = listOf(
                    "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800",
                    "https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=800"
                ),
                price = 299,
                rate = 4.5f,
                salePercentage = 24,
                saleType = "flash_sale",
                colors = listOf(ProductColorModel("M", 10, "#40BFFF"), ProductColorModel("L", 5, "#FB7181")),
                sizes = listOf(ProductSizeModel("6", 5), ProductSizeModel("6.5", 8), ProductSizeModel("7", 10))
            ),
            ProductModel(
                id = "p2",
                name = "FS - QUAPRI Leather Backpack",
                description = "Premium genuine leather backpack crafted for daily commuters, laptop storage, and elegant casual style.",
                categoriesIDs = listOf("3", "4"),
                images = listOf("https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800"),
                price = 199,
                rate = 4.8f,
                salePercentage = 30,
                saleType = "flash_sale",
                colors = listOf(ProductColorModel("One Size", 15, "#223263")),
                sizes = listOf(ProductSizeModel("Standard", 15))
            ),
            ProductModel(
                id = "p3",
                name = "MS - Air Jordan 1 Retro High",
                description = "Iconic basketball sneaker featuring genuine leather upper, encapsulated Air-Sole unit, and rubber outsole for traction.",
                categoriesIDs = listOf("5"),
                images = listOf("https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=800"),
                price = 349,
                rate = 4.9f,
                salePercentage = 50,
                saleType = "mega_sale",
                colors = listOf(ProductColorModel("L", 8, "#FB7181")),
                sizes = listOf(ProductSizeModel("8", 4), ProductSizeModel("9", 6))
            ),
            ProductModel(
                id = "p4",
                name = "MS - Slim Fit Business Shirt",
                description = "100% breathable cotton slim fit shirt ideal for office wear and formal events.",
                categoriesIDs = listOf("1"),
                images = listOf("https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=800"),
                price = 99,
                rate = 4.3f,
                salePercentage = 40,
                saleType = "mega_sale",
                colors = listOf(ProductColorModel("M", 20, "#FFFFFF")),
                sizes = listOf(ProductSizeModel("S", 5), ProductSizeModel("M", 10), ProductSizeModel("L", 5))
            ),
            ProductModel(
                id = "p5",
                name = "Recommended - Smart Watch Pro 5",
                description = "Advanced fitness tracker with AMOLED display, heart rate monitor, GPS, and 7-day battery life.",
                categoriesIDs = listOf("7", "8"),
                images = listOf("https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800"),
                price = 249,
                rate = 4.7f,
                salePercentage = 15,
                saleType = "recommended",
                colors = listOf(ProductColorModel("Standard", 12, "#223263")),
                sizes = listOf(ProductSizeModel("44mm", 12))
            )
        )
    }

    companion object {
        private const val TAG = "ProductsRepositoryImpl"
    }
}