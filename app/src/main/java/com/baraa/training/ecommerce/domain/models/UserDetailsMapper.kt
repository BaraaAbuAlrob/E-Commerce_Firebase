package com.baraa.training.ecommerce.domain.models

import com.baraa.training.ecommerce.data.models.user.UserDetailsModel
import com.baraa.training.ecommerce.data.models.user.UserDetailsPreferences

fun UserDetailsPreferences.toUserDetailsModel(): UserDetailsModel {
    return UserDetailsModel(
        createdAt = createdAt,
        id = id,
        email = email,
        name = name,
        disabled = disabled,
        reviews = reviewsList
    )
}

fun UserDetailsModel.toUserDetailsPreferences(): UserDetailsPreferences {
    return UserDetailsPreferences.newBuilder()
        .setCreatedAt(createdAt ?: 0)
        .setId(id)
        .setEmail(email)
        .setName(name)
        .setDisabled(disabled ?: false)
        .addAllReviews(reviews?.toList() ?: emptyList())
        .build()
}