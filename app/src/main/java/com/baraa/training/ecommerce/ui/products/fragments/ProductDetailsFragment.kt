package com.baraa.training.ecommerce.ui.products.fragments

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.baraa.training.ecommerce.R
import com.baraa.training.ecommerce.databinding.FragmentProductDetailsBinding
import com.baraa.training.ecommerce.ui.common.fragments.BaseFragment
import com.baraa.training.ecommerce.ui.common.views.CircleView
import com.baraa.training.ecommerce.ui.common.views.sliderIndicatorsView
import com.baraa.training.ecommerce.ui.common.views.updateIndicators
import com.baraa.training.ecommerce.ui.products.adapter.ProductImagesAdapter
import com.baraa.training.ecommerce.ui.products.model.ProductUIModel
import com.baraa.training.ecommerce.ui.products.viewmodel.ProductDetailsViewModel
import com.baraa.training.ecommerce.ui.theme.EcommerceTheme
import com.baraa.training.ecommerce.utils.DepthPageTransformer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetailsFragment :
    BaseFragment<FragmentProductDetailsBinding, ProductDetailsViewModel>() {

    override val viewModel: ProductDetailsViewModel by activityViewModels()

    override fun getLayoutResId(): Int = R.layout.fragment_product_details

    override fun init() {
        binding.backIv.setOnClickListener {
            requireActivity().finish()
        }

        binding.btnAddToCart.setOnClickListener {
            viewModel.addToCart {
                Toast.makeText(requireContext(), "Added to Cart!", Toast.LENGTH_SHORT).show()
            }
        }

        initViewModel()
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            viewModel.productDetailsState.collectLatest {
                initView(it)
            }
        }
    }

    private fun initView(it: ProductUIModel) {
        it.name.let { binding.titleTv.text = it }
        initImagesView(it.images)
        initComposeViews()
    }

    private fun initComposeViews() {
        binding.composeView.setContent {
            EcommerceTheme {
                val product = viewModel.productDetailsState.collectAsState().value
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF223263)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row {
                        Text(
                            text = "$${product.price}.00",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF40BFFF),
                                fontSize = 20.sp
                            )
                        )
                        if (product.salePercentage != null && product.salePercentage > 0) {
                            Text(
                                text = "  ${product.salePercentage}% Off",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFB7181)
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF223263)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = product.description.ifEmpty { "High quality item crafted with premium materials for long lasting comfort." },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF9098B1)
                        )
                    )
                }
            }
        }
    }

    private var indicators = mutableListOf<CircleView>()
    private fun initImagesView(images: List<String>) {
        val displayImages = if (images.isEmpty()) listOf("https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800") else images

        sliderIndicatorsView(
            requireContext(),
            binding.productImagesViewPager,
            binding.indicatorView,
            indicators,
            displayImages.size
        )
        binding.productImagesViewPager.apply {
            adapter = ProductImagesAdapter(displayImages)
            setPageTransformer(DepthPageTransformer())

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    updateIndicators(requireContext(), indicators, position)
                }
            })
        }
    }

    companion object
}