package com.baraa.training.ecommerce.data.repository.home

import android.util.Log
import com.baraa.training.ecommerce.data.models.Resource
import com.baraa.training.ecommerce.data.models.sale_ads.SalesAdModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SalesAdsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : SalesAdsRepository {
    override  fun getSalesAds() = flow {
        try {
            Log.d(TAG, "getSalesAds: while loading...")
            emit(Resource.Loading())
            val salesAds = firestore.collection("sales_ads")
                    .get().await().toObjects(SalesAdModel::class.java)
            Log.d(TAG, "Success")
            emit(Resource.Success(salesAds.map { it.toUIModel() }))
        } catch (e: Exception) {
            Log.d(TAG, "Error, the exception message is:   ${e.message}")
            emit(Resource.Error(e))
        }
    }

    companion object {
        private const val TAG = "SalesAdsRepositoryImpl"
    }

    suspend fun getPagingSales(){
        val salesAds =  firestore.collection("sales_ads").limit(10).get().await()
        val lstDocument = salesAds.documents.last()

        getNextPage(lstDocument)
    }

    private suspend fun getNextPage(lastDocument: DocumentSnapshot){
        val salesAds =  firestore.collection("sales_ads").startAfter(lastDocument).limit(10).get().await()
    }
}