package com.baraa.training.ecommerce.data.repository.home

import com.baraa.training.ecommerce.data.models.Resource
import com.baraa.training.ecommerce.ui.home.model.SalesAdUIModel
import kotlinx.coroutines.flow.Flow

interface SalesAdsRepository {
    fun getSalesAds(): Flow<Resource<List<SalesAdUIModel>>>
}