package com.baraa.training.ecommerce.ui.products.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.baraa.training.ecommerce.data.models.products.ProductSizeModel
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class ProductUIModel(
    val id: String,
    val name: String,
    val description: String,
    val colors: List<ProductColorUIModel>,
    val sizes: List<ProductSizeModel>,
    val categoriesIDs: List<String>,
    val images: List<String>,
    val price: Int,
    val rate: Float,
    val priceAfterSale: Int? = null,
    val salePercentage: Int?,
    val saleType: String?,
    val currencySymbol: String = ""
) : Parcelable {

    fun getFormattedPrice(): String {
        return "$currencySymbol$price"
    }

    fun getFormattedPriceAfterSale(): String {
        if (saleType == null || salePercentage == null) return getFormattedPrice()
        val newPrice = salePercentage.let { price.minus(price * it / 100) }
        return "$currencySymbol$newPrice"
    }

    fun getFormattedSale(): String {
        return "$salePercentage% Off"
    }

    fun getFirstImage(): String {
        return images.firstOrNull() ?: ""
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + categoriesIDs.hashCode()
        result = 31 * result + images.hashCode()
        result = 31 * result + price
        result = 31 * result + rate.hashCode()
        result = 31 * result + (salePercentage ?: 0)
        result = 31 * result + (saleType?.hashCode() ?: 0)
        result = 31 * result + colors.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProductUIModel

        if (id != other.id) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (categoriesIDs != other.categoriesIDs) return false
        if (images != other.images) return false
        if (price != other.price) return false
        if (rate != other.rate) return false
        if (salePercentage != other.salePercentage) return false
        if (saleType != other.saleType) return false
        if (colors != other.colors) return false

        return true
    }

    override fun toString(): String {
        return "ProductUIModel(id='$id', name='$name', description='$description', colors=$colors, sizes=$sizes, categoriesIDs=$categoriesIDs, images=$images, price=$price, rate=$rate, priceAfterSale=$priceAfterSale, salePercentage=$salePercentage, saleType=$saleType, currencySymbol='$currencySymbol')"
    }
}

@Keep
@Parcelize
data class ProductColorUIModel(
    var size: String? = null, var stock: Int? = null, var color: String? = null
) : Parcelable


@Keep
@Parcelize
data class ProductSizeUIModel(
    var size: String? = null, var stock: Int? = null
) : Parcelable