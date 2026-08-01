package com.baraa.training.ecommerce.ui.cart.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.baraa.training.ecommerce.databinding.FragmentCartBinding
import com.baraa.training.ecommerce.ui.cart.adapter.CartAdapter
import com.baraa.training.ecommerce.ui.cart.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CartViewModel by viewModels()
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
        observeCartState()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onQuantityChanged = { item, newQuantity ->
                viewModel.updateQuantity(item.id, newQuantity)
            },
            onItemDeleted = { item ->
                viewModel.removeItem(item.id)
            }
        )

        binding.cartRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cartAdapter
        }
    }

    private fun setupListeners() {
        binding.btnCheckout.setOnClickListener {
            Toast.makeText(requireContext(), "Order Placed Successfully! Thank you for your purchase.", Toast.LENGTH_LONG).show()
            viewModel.checkout()
        }
    }

    private fun observeCartState() {
        lifecycleScope.launch {
            viewModel.cartUiState.collectLatest { state ->
                cartAdapter.submitList(state.items)

                if (state.items.isEmpty()) {
                    binding.emptyCartLayout.visibility = View.VISIBLE
                    binding.cartScrollView.visibility = View.GONE
                    binding.checkoutContainer.visibility = View.GONE
                } else {
                    binding.emptyCartLayout.visibility = View.GONE
                    binding.cartScrollView.visibility = View.VISIBLE
                    binding.checkoutContainer.visibility = View.VISIBLE

                    binding.subtotalTv.text = "$${state.subtotal}.00"
                    binding.shippingTv.text = "$${state.shippingFee}.00"
                    binding.importChargesTv.text = "$${state.importCharges}.00"
                    binding.totalPriceTv.text = "$${state.totalPrice}.00"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "CartFragment"
    }
}