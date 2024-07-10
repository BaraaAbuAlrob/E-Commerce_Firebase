package com.baraa.training.ecommerce.domain.models

import com.baraa.training.ecommerce.data.models.categories.CategoryModel
import com.baraa.training.ecommerce.ui.home.model.CategoryUIModel

fun CategoryModel.toUIModel(): CategoryUIModel {
    return CategoryUIModel(
        id = id, name = name, icon = icon
    )
}