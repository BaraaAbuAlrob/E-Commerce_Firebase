package com.baraa.training.ecommerce.ui.explore.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.baraa.training.ecommerce.databinding.FragmentExploreBinding
import com.baraa.training.ecommerce.ui.explore.viewmodel.ExploreViewModel
import com.baraa.training.ecommerce.ui.home.adapter.CategoriesAdapter
import com.baraa.training.ecommerce.ui.products.ProductDetailsActivity
import com.baraa.training.ecommerce.ui.products.adapter.ProductAdapter
import com.baraa.training.ecommerce.ui.products.adapter.ProductViewType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ExploreViewModel by viewModels()
    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapters()
        setupSearch()
        observeViewModel()
    }

    private fun setupAdapters() {
        categoriesAdapter = CategoriesAdapter(emptyList()) { category ->
            viewModel.filterByCategory(category.id ?: "")
        }
        binding.exploreCategoriesRv.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoriesAdapter
        }

        productAdapter = ProductAdapter(ProductViewType.GRID) { product ->
            val intent = Intent(requireContext(), ProductDetailsActivity::class.java).apply {
                putExtra(ProductDetailsActivity.PRODUCT_ID_KEY, product.id)
            }
            startActivity(intent)
        }
        binding.exploreProductsRv.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = productAdapter
        }
    }

    private fun setupSearch() {
        binding.exploreSearchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchProducts(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.categoriesState.collectLatest { categories ->
                categoriesAdapter.submitList(categories)
            }
        }

        lifecycleScope.launch {
            viewModel.productsState.collectLatest { products ->
                productAdapter.submitList(products)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "ExploreFragment"
    }
}