package com.baraa.training.ecommerce.ui.products

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.baraa.training.ecommerce.R
import com.baraa.training.ecommerce.ui.products.model.ProductUIModel
import com.baraa.training.ecommerce.ui.products.viewmodel.ProductDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailsActivity : AppCompatActivity() {

    val productUiModel: ProductUIModel? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(PRODUCT_UI_MODEL_EXTRA, ProductUIModel::class.java)
        } else {
            intent.getParcelableExtra(PRODUCT_UI_MODEL_EXTRA)
        }
    }

    val productIdExtra: String? by lazy {
        intent.getStringExtra(PRODUCT_ID_KEY) ?: productUiModel?.id
    }

    private val viewModel: ProductDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)
    }

    companion object {
        private const val TAG = "ProductDetailsActivity"
        const val PRODUCT_UI_MODEL_EXTRA = "PRODUCT_UI_MODEL_EXTRA"
        const val PRODUCT_ID_KEY = "PRODUCT_ID_KEY"
    }
}