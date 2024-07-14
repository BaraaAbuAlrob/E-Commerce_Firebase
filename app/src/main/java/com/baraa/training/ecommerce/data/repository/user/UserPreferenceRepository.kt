package com.baraa.training.ecommerce.data.repository.user

import com.baraa.training.ecommerce.data.models.user.CountryData
import com.baraa.training.ecommerce.data.models.user.UserDetailsPreferences
import com.baraa.training.ecommerce.ui.auth.models.CountryUIModel
import kotlinx.coroutines.flow.Flow

interface UserPreferenceRepository {
    fun getUserDetails(): Flow<UserDetailsPreferences>
    suspend fun updateUserId(userId: String)
    suspend fun getUserId(): Flow<String>
    suspend fun clearUserPreferences()
    suspend fun updateUserDetails(userDetailsPreferences: UserDetailsPreferences)
    suspend fun saveUserCountry(country: CountryUIModel)
    fun getUserCountry():  Flow<CountryData>
}