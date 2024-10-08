package com.baraa.training.ecommerce.data.repository.auth

import com.baraa.training.ecommerce.data.models.auth.CountryModel
import kotlinx.coroutines.flow.Flow

interface CountryRepository {
    fun getCountries(): Flow<List<CountryModel>>
}