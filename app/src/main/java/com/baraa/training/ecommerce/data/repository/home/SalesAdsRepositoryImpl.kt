package com.baraa.training.ecommerce.data.repository.home

import android.util.Log
import com.baraa.training.ecommerce.data.models.Resource
import com.baraa.training.ecommerce.data.models.sale_ads.SalesAdModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SalesAdsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : SalesAdsRepository {
    override  fun getSalesAds() = flow {
        try {
            Log.d(TAG, "getSalesAds: ")
            emit(Resource.Loading())
            val salesAds = firestore.collection("sales_ads")
                    .get().await().toObjects(SalesAdModel::class.java) // Here the Exception occurs.
            Log.d(TAG, "getSalesAds: Success")
            emit(Resource.Success(salesAds.map { it.toUIModel() }))
        } catch (e: Exception) {
            Log.d(TAG, "getSalesAds: Error")
            emit(Resource.Error(e))
        }
    }

    companion object {
        private const val TAG = "SalesAdsRepositoryImpl"
    }
}