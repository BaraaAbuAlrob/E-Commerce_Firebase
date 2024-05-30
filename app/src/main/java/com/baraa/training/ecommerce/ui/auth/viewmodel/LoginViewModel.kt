package com.baraa.training.ecommerce.ui.auth.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.baraa.training.ecommerce.data.models.Resource
import com.baraa.training.ecommerce.data.repository.auth.FirebaseAuthRepository
import com.baraa.training.ecommerce.data.repository.common.AppPreferenceRepository
import com.baraa.training.ecommerce.data.repository.user.UserPreferenceRepository
import com.baraa.training.ecommerce.utils.isValidEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.lang.Exception

class LoginViewModel(
    private val userPrefs: AppPreferenceRepository,
    private val authRepository: FirebaseAuthRepository,
) : ViewModel() {

    private val _loginState = MutableSharedFlow<Resource<String>>()
    val loginState: SharedFlow<Resource<String>> = _loginState.asSharedFlow()

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

    fun login() = viewModelScope.launch {
            val email = email.value
            val password = password.value
            if (isLoginIsValid.first()) {
                authRepository.loginWithEmailAndPassword(email, password).onEach { resource ->
                    Log.d(TAG, "Emitted resource: $resource")
                    when (resource) {
                        is Resource.Success -> {
                            //TODO get user details from the user id
                            _loginState.emit(Resource.Success(resource.data ?: "Empty User Id"))
                        }

                        else -> _loginState.emit(resource)
                    }
                }.launchIn(viewModelScope)
            } else
                _loginState.emit(Resource.Error(Exception("Invalid email or password")))
    }

    fun loginWithGoogle(idToken: String) = viewModelScope.launch {
        authRepository.loginWithGoogle(idToken).onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    //TODO get user details from the user id
                    _loginState.emit(Resource.Success(resource.data ?: "Empty User Id"))
                }

                else -> _loginState.emit(resource)
            }
        }.launchIn(viewModelScope)
    }

    fun loginWithFacebook(token: String) = viewModelScope.launch{
        authRepository.loginWithFacebook(token).onEach { resource ->
            when(resource) {
                is Resource.Success -> {
                    _loginState.emit(Resource.Success(resource.data ?: "Empty user Id"))
                }

                else -> _loginState.emit(resource)
            }
        }.launchIn(viewModelScope)
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}

// create viewmodel factory class
class LoginViewModelFactory(
    private val userPrefs: AppPreferenceRepository,
    private val authRepository: FirebaseAuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return LoginViewModel(userPrefs, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}