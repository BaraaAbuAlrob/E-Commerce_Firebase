package com.baraa.training.ecommerce.ui.auth.fragments

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.baraa.training.ecommerce.BuildConfig
import com.baraa.training.ecommerce.R
import com.baraa.training.ecommerce.data.datasource.datastore.UserPreferencesDataSource
import com.baraa.training.ecommerce.data.models.Resource
import com.baraa.training.ecommerce.data.repository.auth.FirebaseAuthRepositoryImpl
import com.baraa.training.ecommerce.data.repository.user.UserPreferenceRepositoryImpl
import com.baraa.training.ecommerce.databinding.FragmentLoginBinding
import com.baraa.training.ecommerce.ui.auth.viewmodel.LoginViewModel
import com.baraa.training.ecommerce.ui.auth.viewmodel.LoginViewModelFactory
import com.baraa.training.ecommerce.ui.common.views.ProgressDialog
import com.baraa.training.ecommerce.ui.showRetrySnakeBarError
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
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext

class LoginFragment : Fragment() {

    private lateinit var callbackManager: CallbackManager
    private lateinit var loginManager: LoginManager

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

    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        GlobalScope.launch(newSingleThreadContext("colorChanges")) {
            changeEditTextStrokeAndStartDrawableColors()
        }

        callbackManager = CallbackManager.Factory.create()
        loginManager = LoginManager.getInstance()

        initListeners()
        initViewModel()
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
                    }

                    is Resource.Error -> {
                        progressDialog.dismiss()
                        val msg = resource.exception?.message ?: getString(R.string.generic_err_msg)
                        view?.showSnakeBarError(msg)
                        logAuthIssueToCrashlytics(msg, "Login Error")
                    }
                }
            }
        }
    }

    private fun initListeners() {
        binding.googleSigninBtn.setOnClickListener {
            loginWithGoogleRequest()
        }

        binding.facebookSigninBtn.setOnClickListener {
            if (isLoggedIn()) {
                signOut()
            } else {
                loginWithFacebook()
            }
        }
    }

    private fun isLoggedIn(): Boolean {
        val accessToken = AccessToken.getCurrentAccessToken()
        return accessToken != null && !accessToken.isExpired
    }

    private fun signOut() {
        loginManager.logOut()
        Log.d(TAG, "signOut")
    }

    private fun loginWithFacebook() {
        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult) {
                val token = result.accessToken.token
                loginViewModel.loginWithFacebook(token)
            }

            override fun onCancel() {
                // Handle login cancel
            }

            override fun onError(error: FacebookException) {
                val msg = error.message ?: getString(R.string.generic_err_msg)
                view?.showSnakeBarError(msg)
                logAuthIssueToCrashlytics(msg, "Facebook")
            }
        })

        loginManager.logInWithReadPermissions(
            this,
            listOf("email", "pubic_profile")
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun logAuthIssueToCrashlytics(msg: String, provider: String) {
        CrashlyticsUtils.sendCustomLogToCrashlytics<LoginException>(
            msg,
            CrashlyticsUtils.LOGIN_KEY to msg,
            CrashlyticsUtils.LOGIN_PROVIDER to provider,
        )
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        loginViewModel.loginWithGoogle(idToken)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: Exception) {
            val msg = e.message ?: getString(R.string.generic_err_msg)
            view?.showSnakeBarError(msg)
            logAuthIssueToCrashlytics(msg, "Google")
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
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.clientServerId).requestEmail().requestProfile()
            .requestServerAuthCode(BuildConfig.clientServerId).build()

        val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        googleSignInClient.signOut()
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
        // End loginWithGoogleRequest() method

        // Comments...

        /* I'm get the value "clientServerId" from google-services.json file (the firebase file), in this section:
        "services": {
        "appinvite_service": {
          "other_platform_oauth_client": [
            {
              "client_id": "364834184261-5lvjvf7pt82i66bcflps3j8amjitmnoi.apps.googleusercontent.com",   <<<<<<<<<<<<
              "client_type": 3
            }
          ]
        }
      }
        */

        /* I'm stored the id in the build.gradle.kts file, in this section:

        buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        */
        /**forEach {
        it.buildConfigField(
        "String", "clientServerId", "\"364834184261-5lvjvf7pt82i66bcflps3j8amjitmnoi.apps.googleusercontent.com\""
        )
        }
        }

        Don't forget add this features to gradle.properties (Project properties) file, the line is:
        android.defaults.buildfeatures.buildconfig=true

        then Sync and rebuild the project to create (automatically) the BuildConfig.java class
         */
    }

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
        const val TAG = "LoginFragment"
    }
}