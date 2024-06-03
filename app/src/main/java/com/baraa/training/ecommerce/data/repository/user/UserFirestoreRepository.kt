package com.baraa.training.ecommerce.data.repository.user

import com.baraa.training.ecommerce.data.models.Resource
import com.baraa.training.ecommerce.data.models.user.UserDetailsModel
import kotlinx.coroutines.flow.Flow

interface UserFirestoreRepository {
      suspend fun getUserDetails(userId: String): Flow<Resource<UserDetailsModel>>

}