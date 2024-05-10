package com.baraa.training.ecommerce.ui.auth.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.baraa.training.ecommerce.data.models.Resource
import com.baraa.training.ecommerce.data.repository.auth.FirebaseAuthRepository
import com.baraa.training.ecommerce.data.repository.user.UserPreferenceRepository
import com.baraa.training.ecommerce.utils.isValidEmail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.lang.Exception

class LoginViewModel(
    private val userPrefs: UserPreferenceRepository,
    private val authRepository: FirebaseAuthRepository,
) : ViewModel() {

    val loginState = MutableSharedFlow<Resource<String>>()

    val email = MutableStateFlow("")
    val password = MutableStateFlow("")

    /*
    * ال combine عشان أعمل collect لل email و ال password بنفس ال bloke
    * ومسموحلي أستخدم بس لحد 5 flows بداخل ال combine وإذا كان عندي أكثر
    * بقسمهم على قسمين (Two combines)
    */
    private val isLoginIsValid: Flow<Boolean> = combine(email, password) { email, password ->
        email.isValidEmail() && password.length >= 6
    }

    fun login() {
        viewModelScope.launch {
            val email = email.value
            val password = password.value
            if (email.isNotEmpty() && password.isNotEmpty()) {
                authRepository.loginWithEmailAndPassword(email, password).onEach { resource ->
                    Log.d(TAG, "Emitted resource: $resource")
                    when (resource) {
                        is Resource.Loading
                        -> loginState.emit(Resource.Loading())

                        is Resource.Success
                        -> loginState.emit(
                            Resource.Success(
                                resource.data.toString() ?: "Empty User Id"
                            )
                        )

                        is Resource.Error
                        -> loginState.emit(
                            Resource.Error(
                                resource.exception ?: Exception("Unknown error")
                            )
                        )
                    }
                }.launchIn(viewModelScope)
            } else
                loginState.emit(Resource.Error(Exception("Invalid email or password")))
        }
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}

// create viewmodel factory class
class LoginViewModelFactory(
    private val userPrefs: UserPreferenceRepository,
    private val authRepository: FirebaseAuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return LoginViewModel(userPrefs, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}