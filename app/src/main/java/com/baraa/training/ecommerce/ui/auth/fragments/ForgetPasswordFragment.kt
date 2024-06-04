package com.baraa.training.ecommerce.ui.auth.fragments

import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.baraa.training.ecommerce.R
import com.baraa.training.ecommerce.data.models.Resource
import com.baraa.training.ecommerce.databinding.FragmentForgetPasswordBinding
import com.baraa.training.ecommerce.ui.auth.viewmodel.ForgetPasswordViewModel
import com.baraa.training.ecommerce.ui.auth.viewmodel.ForgetPasswordViewModelFactory
import com.baraa.training.ecommerce.ui.common.views.ProgressDialog
import com.baraa.training.ecommerce.ui.showSnakeBarError
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class ForgetPasswordFragment : BottomSheetDialogFragment() {

    private val progressDialog by lazy { ProgressDialog.createProgressDialog(requireActivity()) }

    private val viewModel: ForgetPasswordViewModel by viewModels {
        ForgetPasswordViewModelFactory()
    }

    private var _binding: FragmentForgetPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = viewModel
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeEditTextStrokeAndStartDrawableColors()
        initViewModel()
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            viewModel.forgetPasswordState.collect { state ->
                when (state) {
                    is Resource.Loading -> {
                        // Show loading
                        progressDialog.show()
                    }

                    is Resource.Success -> {
                        progressDialog.dismiss()
                        showSentEmailSuccessDialog()
                    }

                    is Resource.Error -> {
                        // Show error message
                        progressDialog.dismiss()
                        val msg = state.exception?.message ?: getString(R.string.generic_err_msg)
                        Log.d(TAG, "initViewModelError: $msg")
                        view?.showSnakeBarError(msg)
                    }
                }
            }
        }
    }

    private fun showSentEmailSuccessDialog() {
        MaterialAlertDialogBuilder(requireActivity()).setTitle("Reset Password")
            .setMessage("We have sent you an email to reset your password. Please check your email.")
            .setPositiveButton(
                "OK"
            ) { dialog, _ ->
                dialog?.dismiss()
                this@ForgetPasswordFragment.dismiss()
            }.create().show()
    }

    private fun changeEditTextStrokeAndStartDrawableColors() {
        val emailLayout = binding.emailEtLayout
        val emailEditText = binding.emailEtField

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
        emailEditText.addCustomTextWatcher(emailLayout)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val TAG = "ForgetPasswordFragment"
    }
}