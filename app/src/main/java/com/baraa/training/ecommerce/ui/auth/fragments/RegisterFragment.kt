package com.baraa.training.ecommerce.ui.auth.fragments

import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.baraa.training.ecommerce.R
import com.baraa.training.ecommerce.data.models.Resource
import com.baraa.training.ecommerce.databinding.FragmentRegisterBinding
import com.baraa.training.ecommerce.ui.auth.viewmodel.RegisterViewModel
import com.baraa.training.ecommerce.ui.auth.viewmodel.RegisterViewModelFactory
import com.baraa.training.ecommerce.ui.common.views.ProgressDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private val progressDialog by lazy { ProgressDialog.createProgressDialog(requireActivity()) }

    private val registerViewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory(contextValue = requireContext())
    }

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = registerViewModel
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeEditTextStrokeAndStartDrawableColors()
        initViewModel()
        initListeners()
    }

    private fun initListeners() {
        binding.signInTv.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            registerViewModel.registerState.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        progressDialog.show()
                    }

                    is Resource.Success -> {
                        progressDialog.dismiss()
                        showRegisterSuccessDialog()
                    }

                    is Resource.Error -> {
                        progressDialog.dismiss()
                    }
                }
            }
        }
    }

    // create successful register dialog function
    private fun showRegisterSuccessDialog() {
        MaterialAlertDialogBuilder(requireActivity()).setTitle("Login Success")
            .setMessage("We have sent you an email verification link. Please verify your email to login.")
            .setPositiveButton(
                "OK"
            ) { dialog, _ ->
                dialog?.dismiss()
                findNavController().popBackStack()
            }.create().show()
    }

    private fun changeEditTextStrokeAndStartDrawableColors() {
        val nameLayout = binding.nameLayoutEdText
        val nameEditText = binding.nameFiledEdText
        val emailLayout = binding.emailLayoutEdText
        val emailEditText = binding.emailFiledEdText
        val passwordLayout = binding.passwordLayoutEdText
        val passwordEditText = binding.passwordFiledEdText
        val confirmPasswordLayout = binding.confirmPasswordLayoutEdText
        val confirmPasswordEditText = binding.confirmPasswordFiledEdText

        // Extension function to handle text changes and update UI accordingly
        fun EditText.addCustomTextWatcher(layout: TextInputLayout) {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // Not needed for this case
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Not needed for this case
                }

                override fun afterTextChanged(s: Editable?) {
                    val drawable = compoundDrawables[0] // Assuming drawableStart is at index 0
                    drawable?.let {
                        val wrappedDrawable = DrawableCompat.wrap(it)
                        DrawableCompat.setTintMode(wrappedDrawable, PorterDuff.Mode.SRC_IN)
                        val color =
                            if (s.isNullOrEmpty())
                                R.color.neutral_grey
                            else
                                R.color.primary_color

                        layout.boxStrokeColor = ContextCompat.getColor(requireContext(), color)
                        DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(requireContext(), color))
                    }
                }
            })
        }

        // Apply the text watcher to both EditTexts
        nameEditText.addCustomTextWatcher(nameLayout)
        emailEditText.addCustomTextWatcher(emailLayout)
        passwordEditText.addCustomTextWatcher(passwordLayout)
        confirmPasswordEditText.addCustomTextWatcher(confirmPasswordLayout)
    }
}