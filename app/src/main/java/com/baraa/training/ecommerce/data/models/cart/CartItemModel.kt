package com.baraa.training.ecommerce.data.models.cart

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class CartItemModel(
    var id: String = "",
    var productId: String = "",
    var productName: String = "",
    var productImage: String = "",
    var price: Int = 0,
    var quantity: Int = 1,
    var selectedColor: String? = null,
    var selectedSize: String? = null
) : Parcelable
