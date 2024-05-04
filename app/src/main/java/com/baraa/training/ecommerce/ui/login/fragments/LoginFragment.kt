package com.baraa.training.ecommerce.ui.login.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.baraa.training.ecommerce.R
import com.baraa.training.ecommerce.data.repository.user.UserPreferenceRepositoryImplementation
import com.baraa.training.ecommerce.databinding.FragmentLoginBinding
import com.baraa.training.ecommerce.ui.login.viewmodel.LoginViewModel

class LoginFragment : Fragment() {

    val viewModel: LoginViewModel by lazy {
        LoginViewModel(userPrefs = UserPreferenceRepositoryImplementation(requireActivity()))
    }

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeTheETFStrokeColors()
    }

    private fun changeTheETFStrokeColors() {
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
                if (s != null) {
                    emailLayout.boxStrokeColor = ContextCompat.getColor(requireContext(), R.color.primary_color)
                } else {
                    // Set the default stroke color when no text is entered
                    emailLayout.boxStrokeColor = ContextCompat.getColor(requireContext(), R.color.neutral_grey)
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
                if (s != null) {
                    passwordLayout.boxStrokeColor = ContextCompat.getColor(requireContext(), R.color.primary_color)
                } else {
                    // Set the default stroke color when no text is entered
                    passwordLayout.boxStrokeColor = ContextCompat.getColor(requireContext(), R.color.neutral_grey)
                }
            }
        })
    }

    companion object {
        const val TAG = "LoginFragment"
    }
}