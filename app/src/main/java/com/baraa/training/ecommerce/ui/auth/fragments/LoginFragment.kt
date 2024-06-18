package com.baraa.training.ecommerce.ui.auth.fragments

import android.content.Intent
import android.graphics.PorterDuff
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.baraa.training.ecommerce.R
import com.baraa.training.ecommerce.data.models.Resource
import com.baraa.training.ecommerce.databinding.FragmentLoginBinding
import com.baraa.training.ecommerce.ui.auth.getGoogleRequestIntent
import com.baraa.training.ecommerce.ui.auth.viewmodel.LoginViewModel
import com.baraa.training.ecommerce.ui.common.fragments.BaseFragment
import com.baraa.training.ecommerce.ui.home.MainActivity
import com.baraa.training.ecommerce.ui.showSnakeBarError
import com.baraa.training.ecommerce.utils.CrashlyticsUtils
import com.baraa.training.ecommerce.utils.LoginException
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {
    private val callbackManager: CallbackManager by lazy { CallbackManager.Factory.create() }
    private val loginManager: LoginManager by lazy { LoginManager.getInstance() }

    override val viewModel: LoginViewModel by viewModels ()

    override fun getLayoutResId(): Int = R.layout.fragment_login

    override fun init() {
        changeEditTextStrokeAndStartDrawableColors()
        initListeners()
        initViewModel()
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            viewModel.loginState.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        progressDialog.show()
                    }

                    is Resource.Success -> {
                        progressDialog.dismiss()
                        goToHome()
                    }

                    is Resource.Error -> {
                        progressDialog.dismiss()
                        val msg = resource.exception?.message ?: getString(R.string.generic_err_msg)
                        Log.d(TAG, "initViewModelError: $msg")
                        view?.showSnakeBarError(msg)
                    }
                }
            }
        }
    }

    private fun goToHome() {
        requireActivity().startActivity(Intent(activity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        requireActivity().finish()
    }

    private fun initListeners() {
        binding.googleSigninBtn.setOnClickListener {
            loginWithGoogleRequest()
        }
        binding.facebookSigninBtn.setOnClickListener {
            loginWithFacebook()
        }
        binding.registerTv.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.forgotPasswordTv.setOnClickListener {
            val forgetPasswordFragment = ForgetPasswordFragment()
            forgetPasswordFragment.show(parentFragmentManager, "forget-password")
        }
    }

    // ActivityResultLauncher for the sign-in intent
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
            } else {
                view?.showSnakeBarError(getString(R.string.google_sign_in_field_msg))
            }
        }

    private fun loginWithGoogleRequest() {
        val signInIntent = getGoogleRequestIntent(requireActivity())
        launcher.launch(signInIntent)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: Exception) {
            view?.showSnakeBarError(e.message ?: getString(R.string.generic_err_msg))
            val msg = e.message ?: getString(R.string.generic_err_msg)
            logAuthIssueToCrashlytics(msg, "Google")
        }
    }

    private fun logAuthIssueToCrashlytics(msg: String, provider: String) {
        CrashlyticsUtils.sendCustomLogToCrashlytics<LoginException>(
            msg,
            CrashlyticsUtils.LOGIN_KEY to msg,
            CrashlyticsUtils.LOGIN_PROVIDER to provider
        )
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        viewModel.loginWithGoogle(idToken)
    }

    private fun signOut() {
        loginManager.logOut()
        Log.d(TAG, "signOut: ")
    }

    private fun isLoggedIn(): Boolean {
        val accessToken = AccessToken.getCurrentAccessToken()
        return accessToken != null && !accessToken.isExpired
    }

    private fun loginWithFacebook() {
        if (isLoggedIn()) signOut()
        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val token = result.accessToken.token
                Log.d(TAG, "onSuccess: $token")
                firebaseAuthWithFacebook(token)
            }

            override fun onCancel() {
                // Handle login cancel
            }

            override fun onError(error: FacebookException) {
                // Handle login error
                val msg = error.message ?: getString(R.string.generic_err_msg)
                Log.d(TAG, "onError: $msg")
                view?.showSnakeBarError(msg)
                logAuthIssueToCrashlytics(msg, "Facebook")
            }
        })

        loginManager.logInWithReadPermissions(
            this, callbackManager, listOf("email", "public_profile")
        )
    }

    private fun firebaseAuthWithFacebook(token: String) {
        viewModel.loginWithFacebook(token)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        callbackManager.onActivityResult(requestCode, resultCode, data)
//    }

    private fun changeEditTextStrokeAndStartDrawableColors() {
        val emailLayout = binding.emailLayoutEdText
        val emailEditText = binding.emailFiledEdText
        val passwordLayout = binding.passwordLayoutEdText
        val passwordEditText = binding.passwordFiledEdText

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
        passwordEditText.addCustomTextWatcher(passwordLayout)
    }

    companion object {

        private const val TAG = "LoginFragment"
    }
}