package com.baraa.training.ecommerce.data.repository.user

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.baraa.training.ecommerce.data.datasource.DataStoreKeys.IS_USER_LOGGED_IN
import com.baraa.training.ecommerce.data.datasource.DataStoreKeys.USER_ID
import com.baraa.training.ecommerce.data.datasource.dataStore
import com.baraa.training.ecommerce.data.datasource.datastore.UserPreferencesDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferenceRepositoryImplementation(
    private val userPreferencesDataSource: UserPreferencesDataSource
) : UserPreferenceRepository {

    override suspend fun saveLoginState(isLoggedIn: Boolean) {
        userPreferencesDataSource.saveLoginState(isLoggedIn)
    }

    override suspend fun saveUserID(userId: String) {
        userPreferencesDataSource.saveUserID(userId)
    }

    override suspend fun isUserLoggedIn(): Flow<Boolean> {
        return userPreferencesDataSource.isUserLoggedIn
    }

    override fun getUserID(): Flow<String?> {
        return userPreferencesDataSource.userID
    }
}