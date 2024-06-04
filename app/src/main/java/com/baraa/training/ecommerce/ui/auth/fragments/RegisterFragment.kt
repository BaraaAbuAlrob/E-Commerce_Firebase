package com.baraa.training.ecommerce.ui.auth.fragments

import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.baraa.training.ecommerce.BuildConfig
import com.baraa.training.ecommerce.R
import com.baraa.training.ecommerce.data.models.Resource
import com.baraa.training.ecommerce.databinding.FragmentRegisterBinding
import com.baraa.training.ecommerce.ui.auth.viewmodel.RegisterViewModel
import com.baraa.training.ecommerce.ui.auth.viewmodel.RegisterViewModelFactory
import com.baraa.training.ecommerce.ui.common.views.ProgressDialog
import com.baraa.training.ecommerce.ui.showSnakeBarError
import com.baraa.training.ecommerce.utils.CrashlyticsUtils
import com.baraa.training.ecommerce.utils.RegisterException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
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
        binding.googleSignupBtn.setOnClickListener {
            registerWithGoogleRequest()
        }
        binding.facebookSignupBtn.setOnClickListener {
//            loginWithFacebook()
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

    // ActivityResultLauncher for the sign-in intent
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignUpResult(task)
            } else {
                view?.showSnakeBarError(getString(R.string.google_sign_in_field_msg))
            }
        }

    private fun registerWithGoogleRequest() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.clientServerId).requestEmail().requestProfile()
            .requestServerAuthCode(BuildConfig.clientServerId).build()

        val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        googleSignInClient.signOut()
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private fun handleSignUpResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: Exception) {
            view?.showSnakeBarError(e.message ?: getString(R.string.generic_err_msg))
            val msg = e.message ?: getString(R.string.generic_err_msg)
            logAuthIssueToCrashlytics(msg, "Google")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        registerViewModel.signUpWithGoogle(idToken)
    }


    private fun logAuthIssueToCrashlytics(msg: String, provider: String) {
        CrashlyticsUtils.sendCustomLogToCrashlytics<RegisterException>(
            msg,
            CrashlyticsUtils.REGISTER_KEY to msg,
            CrashlyticsUtils.LOGIN_PROVIDER to provider,
        )
    }

    // create successful register dialog function
    private fun showRegisterSuccessDialog() {
        MaterialAlertDialogBuilder(requireActivity()).setTitle("Register Success")
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