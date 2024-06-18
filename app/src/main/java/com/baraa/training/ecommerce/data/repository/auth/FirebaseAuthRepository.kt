package com.baraa.training.ecommerce.data.repository.auth

import com.baraa.training.ecommerce.data.models.Resource
import com.baraa.training.ecommerce.data.models.auth.RegisterRequestModel
import com.baraa.training.ecommerce.data.models.auth.RegisterResponseModel
import com.baraa.training.ecommerce.data.models.user.UserDetailsModel
import kotlinx.coroutines.flow.Flow

interface FirebaseAuthRepository {
    suspend fun loginWithEmailAndPassword(
        email: String, password: String
    ): Flow<Resource<UserDetailsModel>>

    suspend fun loginWithGoogle(
        idToken: String
    ): Flow<Resource<UserDetailsModel>>

    suspend fun loginWithFacebook(
        token: String
    ): Flow<Resource<UserDetailsModel>>

    suspend fun registerEmailAndPasswordWithAPI(
        registerRequestModel: RegisterRequestModel
    ): Flow<Resource<RegisterResponseModel>>

    suspend fun registerWithEmailAndPassword(
        name: String, email: String, password: String
    ): Flow<Resource<UserDetailsModel>>

    suspend fun registerWithGoogleWithAPI(
        idToken: String
    ): Flow<Resource<RegisterResponseModel>>

    suspend fun registerWithGoogle(
        idToken: String
    ): Flow<Resource<UserDetailsModel>>

    suspend fun registerWithFacebookWithAPI(
        token: String
    ): Flow<Resource<RegisterResponseModel>>

    suspend fun registerWithFacebook(
        token: String
    ): Flow<Resource<UserDetailsModel>>

    suspend fun sendUpdatePasswordEmail(
        email: String
    ): Flow<Resource<String>>

    fun logout()
}