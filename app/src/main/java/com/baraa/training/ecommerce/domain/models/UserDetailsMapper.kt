package com.baraa.training.ecommerce.domain.models

import com.baraa.training.ecommerce.data.models.user.CountryData
import com.baraa.training.ecommerce.data.models.user.UserDetailsModel
import com.baraa.training.ecommerce.data.models.user.UserDetailsPreferences

fun UserDetailsPreferences.toUserDetailsModel(): UserDetailsModel {
    return UserDetailsModel(
        id = id,
        email = email,
        name = name,
        reviews = reviewsList
    )
}

fun UserDetailsModel.toUserDetailsPreferences(countryData: CountryData): UserDetailsPreferences {
    return UserDetailsPreferences.newBuilder()
        .setId(id)
        .setEmail(email)
        .setName(name)
        .addAllReviews(reviews?.toList() ?: emptyList())
        .setCountry(countryData)
        .build()
}