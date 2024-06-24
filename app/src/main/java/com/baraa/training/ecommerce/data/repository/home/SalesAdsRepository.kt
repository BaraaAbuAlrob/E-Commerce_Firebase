package com.baraa.training.ecommerce.data.repository.home

import com.baraa.training.ecommerce.data.models.sale_ads.SalesAdModel
import com.baraa.training.ecommerce.data.models.Resource
import kotlinx.coroutines.flow.Flow

interface SalesAdsRepository {
    fun getSalesAds(): Flow<Resource<List<SalesAdModel>>>
}