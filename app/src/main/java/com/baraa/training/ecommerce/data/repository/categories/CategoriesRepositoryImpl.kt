package com.baraa.training.ecommerce.data.repository.categories

import android.util.Log
import com.baraa.training.ecommerce.data.models.Resource
import com.baraa.training.ecommerce.data.models.categories.CategoryModel
import com.baraa.training.ecommerce.domain.models.toUIModel
import com.baraa.training.ecommerce.ui.home.model.CategoryUIModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CategoriesRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CategoriesRepository {
    override fun getCategories(): Flow<Resource<List<CategoryUIModel>>> = flow {
            emit(Resource.Loading())
            val result = try {
                val categories = firestore.collection("categories").get().await()
                    .toObjects(CategoryModel::class.java)
                Log.d(TAG, "categories = $categories")

                val resultList = if (categories.isNotEmpty()) {
                    categories.map { it.toUIModel() }
                } else {
                    getFallbackCategories()
                }
                Resource.Success(resultList)
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "getCategories: error, using fallback", e)
                Resource.Success(getFallbackCategories())
            }
            emit(result)
        }

    private fun getFallbackCategories(): List<CategoryUIModel> {
        return listOf(
            CategoryUIModel("1", "Man Shirt", "https://i.imgur.com/QkIa5tT.png"),
            CategoryUIModel("2", "Dress", "https://i.imgur.com/2nL6q80.png"),
            CategoryUIModel("3", "Man Bag", "https://i.imgur.com/L7p4Sg5.png"),
            CategoryUIModel("4", "Woman Bag", "https://i.imgur.com/39wF51L.png"),
            CategoryUIModel("5", "Man Shoes", "https://i.imgur.com/z4bW9Ww.png"),
            CategoryUIModel("6", "High Heels", "https://i.imgur.com/N6uD97b.png"),
            CategoryUIModel("7", "Smartphone", "https://i.imgur.com/W2dO3gH.png"),
            CategoryUIModel("8", "Electronics", "https://i.imgur.com/v8tT9N1.png")
        )
    }

    companion object {
        private const val TAG = "CategoriesRepositoryImp"
    }
}