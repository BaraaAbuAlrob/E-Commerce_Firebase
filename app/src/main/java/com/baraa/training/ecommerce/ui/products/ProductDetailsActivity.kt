package com.baraa.training.ecommerce.ui.products

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.baraa.training.ecommerce.R
import com.baraa.training.ecommerce.ui.products.model.ProductUIModel
import com.baraa.training.ecommerce.ui.products.viewmodel.ProductDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetailsActivity : AppCompatActivity() {

    val productUiModel: ProductUIModel by lazy {
        val product = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(PRODUCT_UI_MODEL_EXTRA, ProductUIModel::class.java)
        } else {
            intent.getParcelableExtra(PRODUCT_UI_MODEL_EXTRA)
        }

        product ?: throw IllegalArgumentException("ProductUIModel is required")
    }

    private val viewModel: ProductDetailsViewModel by viewModels()

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        lifecycleScope.launch {
            viewModel.productDetailsState.collectLatest {
                // Update UI
                Log.d(TAG, "Product details: $it")
            }
        }
    }

    companion object {
        private const val TAG = "ProductDetailsActivity"
        const val PRODUCT_UI_MODEL_EXTRA = "PRODUCT_UI_MODEL_EXTRA"
    }
}