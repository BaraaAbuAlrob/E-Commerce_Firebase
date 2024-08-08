package com.baraa.training.ecommerce.domain.models

import com.baraa.training.ecommerce.data.models.SpecialSectionModel
import com.baraa.training.ecommerce.ui.home.model.SpecialSectionUIModel

fun SpecialSectionModel.toSpecialSectionUIModel(): SpecialSectionUIModel {
    return SpecialSectionUIModel(
        id = id,
        title = title,
        description = description,
        type = type,
        image = image,
        enabled = enabled
    )
}

fun SpecialSectionUIModel.toSpecialSectionModel(): SpecialSectionModel {
    return SpecialSectionModel(
        id = id,
        title = title,
        description = description,
        type = type,
        image = image,
        enabled = enabled
    )
}