package com.baraa.training.ecommerce.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.baraa.training.ecommerce.data.datasource.DataStoreKeys.E_COMMERCE_PREFERENCE

object DataStoreKeys {
    const val E_COMMERCE_PREFERENCE = "e_commerce_preference"
    val IS_USER_LOGGED_IN = booleanPreferencesKey("is_user_logged_in")
    val USER_ID = stringPreferencesKey("is_user_logged_in")
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = E_COMMERCE_PREFERENCE)