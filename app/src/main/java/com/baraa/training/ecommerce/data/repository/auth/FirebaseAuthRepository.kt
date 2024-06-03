package com.baraa.training.ecommerce.data.repository.auth

import com.baraa.training.ecommerce.data.models.Resource
import com.baraa.training.ecommerce.data.models.user.UserDetailsModel
import kotlinx.coroutines.flow.Flow

interface FirebaseAuthRepository {
    suspend fun loginWithEmailAndPassword(
        email: String, password: String
    ): Flow<Resource<UserDetailsModel>>

    suspend fun loginWithGoogle(
        idToken: String
    ): Flow<Resource<UserDetailsModel>>
    suspend fun loginWithFacebook(token: String): Flow<Resource<UserDetailsModel>>

    fun logout()
}