package com.baraa.training.ecommerce.domain.models

import com.baraa.training.ecommerce.data.models.auth.CountryModel
import com.baraa.training.ecommerce.ui.auth.models.CountryUIModel

fun CountryModel.toUIModel(): CountryUIModel {
    return CountryUIModel(
        id = id,
        name = name,
        code = code,
        currency = currency,
        image = image,
        currencySymbol = currencySymbol
    )
}