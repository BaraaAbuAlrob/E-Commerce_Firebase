package com.baraa.training.ecommerce.ui.account.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.baraa.training.ecommerce.databinding.FragmentAccountBinding
import com.baraa.training.ecommerce.ui.account.viewmodel.AccountViewModel
import com.baraa.training.ecommerce.ui.auth.AuthActivity
import com.baraa.training.ecommerce.ui.auth.fragments.CountriesFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.itemCountry.setOnClickListener {
            val countryFragment = CountriesFragment()
            countryFragment.show(parentFragmentManager, "country-fragment")
        }

        binding.itemOrders.setOnClickListener {
            Toast.makeText(requireContext(), "Order History is empty.", Toast.LENGTH_SHORT).show()
        }

        binding.itemProfile.setOnClickListener {
            Toast.makeText(requireContext(), "Profile details loaded.", Toast.LENGTH_SHORT).show()
        }

        binding.itemLogout.setOnClickListener {
            viewModel.signOut {
                val intent = Intent(requireActivity(), AuthActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.userState.collectLatest { user ->
                if (user != null) {
                    binding.userNameTv.text = user.name ?: "Valued Customer"
                    binding.userEmailTv.text = user.email ?: "user@ecommerce.com"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "AccountFragment"
    }
}