package com.baraa.training.ecommerce.data.repository.auth

import android.util.Log
import com.baraa.training.ecommerce.data.models.auth.CountryModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CountryRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CountryRepository {
    override fun getCountries(): Flow<List<CountryModel>> = flow {
        val countries = firestore.collection("countries").get().await().toObjects(CountryModel::class.java)
        Log.d("CountryRepositoryImpl", "getCountries: $countries")
        emit(countries)
    }
}