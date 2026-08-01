package com.baraa.training.ecommerce.ui.offers

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.baraa.training.ecommerce.databinding.FragmentOffersBinding
import com.baraa.training.ecommerce.ui.offers.viewmodel.OffersViewModel
import com.baraa.training.ecommerce.ui.products.ProductDetailsActivity
import com.baraa.training.ecommerce.ui.products.adapter.ProductAdapter
import com.baraa.training.ecommerce.ui.products.adapter.ProductViewType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OffersFragment : Fragment() {

    private var _binding: FragmentOffersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OffersViewModel by viewModels()
    private lateinit var flashSaleAdapter: ProductAdapter
    private lateinit var megaSaleAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOffersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapters()
        observeViewModel()
    }

    private fun setupAdapters() {
        flashSaleAdapter = ProductAdapter(ProductViewType.GRID) { product ->
            val intent = Intent(requireContext(), ProductDetailsActivity::class.java).apply {
                putExtra(ProductDetailsActivity.PRODUCT_ID_KEY, product.id)
            }
            startActivity(intent)
        }
        binding.flashSaleRv.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = flashSaleAdapter
        }

        megaSaleAdapter = ProductAdapter(ProductViewType.GRID) { product ->
            val intent = Intent(requireContext(), ProductDetailsActivity::class.java).apply {
                putExtra(ProductDetailsActivity.PRODUCT_ID_KEY, product.id)
            }
            startActivity(intent)
        }
        binding.megaSaleRv.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = megaSaleAdapter
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.flashSaleState.collectLatest { list ->
                flashSaleAdapter.submitList(list)
            }
        }

        lifecycleScope.launch {
            viewModel.megaSaleState.collectLatest { list ->
                megaSaleAdapter.submitList(list)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "OffersFragment"
    }
}