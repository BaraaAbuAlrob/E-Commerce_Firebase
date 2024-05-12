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
import com.baraa.training.ecommerce.ui.showSnakeBarError
import com.baraa.training.ecommerce.utils.CrashlyticsUtils
import com.baraa.training.ecommerce.utils.LoginException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext

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

    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        GlobalScope.launch(newSingleThreadContext("colorChanges")){
            changeEditTextStrokeAndStartDrawableColors()
        }

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
        binding.googleSigninBtn.setOnClickListener {
            loginWithGoogleRequest()
        }
    }

    private fun logAuthIssueToCrashlytics(msg: String, provider: String) {
        CrashlyticsUtils.sendCustomLogToCrashlytics<LoginException>(
            msg,
            CrashlyticsUtils.LOGIN_KEY to msg,
            CrashlyticsUtils.LOGIN_PROVIDER to provider,
        )
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val email = task.result.user?.email
                    Log.d(TAG, "firebaseAuthWithGoogle: $email")
                    // Proceed to next activity or update UI
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
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