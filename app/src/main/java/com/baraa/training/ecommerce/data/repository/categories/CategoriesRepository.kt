package com.baraa.training.ecommerce.data.repository.categories

import com.baraa.training.ecommerce.data.models.Resource
import com.baraa.training.ecommerce.ui.home.model.CategoryUIModel
import kotlinx.coroutines.flow.Flow

interface CategoriesRepository {
    fun getCategories(): Flow<Resource<List<CategoryUIModel>>>
}