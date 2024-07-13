package com.baraa.training.ecommerce.data.repository.user

import android.app.Application
import com.baraa.training.ecommerce.data.datasource.datastore.userDetailsDataStore
import com.baraa.training.ecommerce.data.models.user.CountryData
import com.baraa.training.ecommerce.data.models.user.UserDetailsPreferences
import com.baraa.training.ecommerce.ui.auth.models.CountryUIModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferenceRepositoryImpl @Inject constructor(val context: Application) :
    UserPreferenceRepository {
    override fun getUserDetails(): Flow<UserDetailsPreferences> {
        return context.userDetailsDataStore.data
    }

    override suspend fun updateUserId(userId: String) {
        context.userDetailsDataStore.updateData { preferences ->
            preferences.toBuilder().setId(userId).build()
        }
    }

    override suspend fun getUserId(): Flow<String> {
        return context.userDetailsDataStore.data.map { it.id }
    }

    override suspend fun clearUserPreferences() {
        context.userDetailsDataStore.updateData { preferences ->
            preferences.toBuilder().clear().build()
        }
    }

    override suspend fun updateUserDetails(userDetailsPreferences: UserDetailsPreferences) {
        context.userDetailsDataStore.updateData { userDetailsPreferences }
    }

    override suspend fun saveUserCountry(country: CountryUIModel) {
        val countryData = CountryData.newBuilder().setId(country.id).setCode(country.code)
            .setName(country.name).setCurrency(country.currency).setCurrencySymbol(country.currencySymbol).build()

        context.userDetailsDataStore.updateData { preferences ->
            preferences.toBuilder().setCountry(countryData).build()
        }
    }

    override suspend fun getUserCountry(
    ): Flow<CountryData> = context.userDetailsDataStore.data.map { it.country }
}