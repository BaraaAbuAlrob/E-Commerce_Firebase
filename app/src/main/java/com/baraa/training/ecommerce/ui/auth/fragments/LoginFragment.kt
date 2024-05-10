package com.baraa.training.ecommerce.ui.auth.fragments

import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.baraa.training.ecommerce.R
import com.baraa.training.ecommerce.data.datasource.datastore.UserPreferencesDataSource
import com.baraa.training.ecommerce.data.models.Resource
import com.baraa.training.ecommerce.data.repository.auth.FirebaseAuthRepositoryImpl
import com.baraa.training.ecommerce.data.repository.user.UserPreferenceRepositoryImpl
import com.baraa.training.ecommerce.databinding.FragmentLoginBinding
import com.baraa.training.ecommerce.ui.auth.viewmodel.AuthViewModel
import com.baraa.training.ecommerce.ui.auth.viewmodel.LoginViewModel
import com.baraa.training.ecommerce.ui.auth.viewmodel.LoginViewModelFactory
import com.baraa.training.ecommerce.ui.common.views.ProgressDialog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private val progressDialog by lazy { ProgressDialog.createProgressDialog(requireActivity()) }

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(
            userPrefs = UserPreferenceRepositoryImpl(
                UserPreferencesDataSource(
                    requireActivity()
                )
            ),
            authRepository = FirebaseAuthRepositoryImpl()
        )
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = loginViewModel
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeEditTextStrokeAndStartDrawableColors()
        initListeners()
        initViewModel()

        Log.d(TAG, "onViewCreated: ")
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            loginViewModel.loginState.collect { resource ->
                Log.d(TAG, "initViewModel: $resource")
                when (resource) {
                    is Resource.Loading -> {
                        progressDialog.show()
                    }

                    is Resource.Success -> {
                        progressDialog.dismiss()
                        Toast.makeText(
                            requireContext(), "Login successfully", Toast.LENGTH_SHORT
                        ).show()
                    }

                    is Resource.Error -> {
                        progressDialog.dismiss()
                        Log.d(TAG, "Resource.Error: ${resource.exception?.message}")
                        Toast.makeText(
                            requireContext(), resource.exception?.message, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun initListeners() {
        binding.loginBtn.setOnClickListener {
            loginViewModel.login()
        }
    }

    private fun changeEditTextStrokeAndStartDrawableColors() {
        val emailLayout = binding.emailLayoutEdText
        val emailEditText = binding.emailFiledEdText
        val passwordLayout = binding.passwordLayoutEdText
        val passwordEditText = binding.passwordFiledEdText

        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed for this case
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not needed for this case
            }

            override fun afterTextChanged(s: Editable?) {

                // Change the tint of the drawableStart only when there is text
                val drawable =
                    emailEditText.compoundDrawables[0] // Assuming drawableStart is at index 0
                val wrappedDrawable = DrawableCompat.wrap(drawable!!)
                DrawableCompat.setTintMode(wrappedDrawable, PorterDuff.Mode.SRC_IN)
                if (!s.isNullOrEmpty()) {
                    // Change the stroke color
                    emailLayout.boxStrokeColor =
                        ContextCompat.getColor(requireContext(), R.color.primary_color)

                    //For the drawableStart color
                    DrawableCompat.setTint(
                        wrappedDrawable,
                        ContextCompat.getColor(requireContext(), R.color.primary_color)
                    )
                } else {
                    // Set the default stroke color when no text is entered
                    emailLayout.boxStrokeColor =
                        ContextCompat.getColor(requireContext(), R.color.neutral_grey)

                    // Reset the tint if there is no text
                    DrawableCompat.setTint(
                        wrappedDrawable,
                        ContextCompat.getColor(requireContext(), R.color.neutral_grey)
                    )
                }
            }
        })

        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed for this case
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not needed for this case
            }

            override fun afterTextChanged(s: Editable?) {
                // Change the tint of the drawableStart only when there is text
                val drawable =
                    passwordEditText.compoundDrawables[0] // Assuming drawableStart is at index 0
                val wrappedDrawable = DrawableCompat.wrap(drawable!!)
                DrawableCompat.setTintMode(wrappedDrawable, PorterDuff.Mode.SRC_IN)
                if (!s.isNullOrEmpty()) {
                    // Change the stroke color
                    passwordLayout.boxStrokeColor =
                        ContextCompat.getColor(requireContext(), R.color.primary_color)

                    //For the drawableStart color
                    DrawableCompat.setTint(
                        wrappedDrawable,
                        ContextCompat.getColor(requireContext(), R.color.primary_color)
                    )
                } else {
                    // Set the default stroke color when no text is entered
                    passwordLayout.boxStrokeColor =
                        ContextCompat.getColor(requireContext(), R.color.neutral_grey)

                    // Reset the tint if there is no text
                    DrawableCompat.setTint(
                        wrappedDrawable,
                        ContextCompat.getColor(requireContext(), R.color.neutral_grey)
                    )
                }
            }
        })
    }

    companion object {
        const val TAG = "LoginFragment"
    }
}