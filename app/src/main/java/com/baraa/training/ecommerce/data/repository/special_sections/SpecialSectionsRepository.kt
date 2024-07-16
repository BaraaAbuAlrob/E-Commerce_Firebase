package com.baraa.training.ecommerce.data.repository.special_sections

import com.baraa.training.ecommerce.data.models.SpecialSectionModel
import kotlinx.coroutines.flow.Flow

interface SpecialSectionsRepository {
    fun recommendProductsSection(): Flow<SpecialSectionModel?>
}