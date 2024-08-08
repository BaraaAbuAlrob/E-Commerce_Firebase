package com.baraa.training.ecommerce.data.repository.special_sections

import android.util.Log
import com.baraa.training.ecommerce.data.models.SpecialSectionModel
import com.baraa.training.ecommerce.data.models.SpecialSections
import com.baraa.training.ecommerce.utils.CrashlyticsUtils
import com.baraa.training.ecommerce.utils.SpecialSectionsException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SpecialSectionsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : SpecialSectionsRepository {

    override fun recommendProductsSection(): Flow<SpecialSectionModel?> = flow {
        try {
            val specialSection = firestore.collection("special_sections")
                .document(SpecialSections.RECOMMENDED_PRODUCTS.id).get().await()
                .toObject(SpecialSectionModel::class.java)
            Log.d(TAG, "recommendProductsSection: success $specialSection")

            emit(specialSection)

        } catch (e: Exception) {
            Log.d(TAG, "recommendProductsSection: Error fetching recommended products ${e.message}")
            val msg = e.message ?: "Error fetching recommended products"
            CrashlyticsUtils.sendCustomLogToCrashlytics<SpecialSectionsException>(
                msg, Pair(CrashlyticsUtils.SPECIAL_SECTIONS, msg)
            )
            emit(null)
        }
    }

    companion object {
        private const val TAG = "SpecialSectionsRepositoryImpl"
    }
}