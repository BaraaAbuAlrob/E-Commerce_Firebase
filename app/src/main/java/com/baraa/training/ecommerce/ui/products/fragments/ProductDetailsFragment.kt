package com.baraa.training.ecommerce.ui.products.fragments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
        initComposViews()
    }

    private fun initComposViews() {
        binding.composeView.setContent {
            EcommerceTheme {
                Column {
                    val product = viewModel.productDetailsState.collectAsState().value
                    Row {
                        Text(
                            text = product.description,
                            style = MaterialTheme.typography.titleLarge // or style = ECommerceTypography.titleLarge

                        )
                    }

                    var count by remember {
                        mutableIntStateOf(0)
                    }

                    Text(text = "compose test recomposition, count: $count",
                        modifier = Modifier.clickable {
                            count++
                        }
                    )
                }
            }
        }
    }

    private var indicators = mutableListOf<CircleView>()
    private fun initImagesView(images: List<String>) {

        sliderIndicatorsView(
            requireContext(),
            binding.productImagesViewPager,
            binding.indicatorView,
            indicators,
            images.size
        )
        binding.productImagesViewPager.apply {
            adapter = ProductImagesAdapter(images)
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