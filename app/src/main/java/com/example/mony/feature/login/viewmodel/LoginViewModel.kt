package com.example.mony.feature.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    class Success() : LoginState()
    class Error() : LoginState()
}

@Suppress("DEPRECATION")
class LoginViewModel(
    private val auth: FirebaseAuth = Firebase.auth
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                auth.currentUser?.reload()?.await()
                auth.currentUser?.email?.let { email ->
                    _loginState.value = LoginState.Success()
                } ?: run {
                    _loginState.value = LoginState.Idle
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error()
            }
        }
    }

    fun handleGoogleSignInResult(account: GoogleSignInAccount?) {
        viewModelScope.launch(Dispatchers.IO) {
            _loginState.value = LoginState.Loading
            try {
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                val result = auth.signInWithCredential(credential).await()

                result.user?.let { user ->
                    _loginState.value = LoginState.Success()
                } ?: run {
                    _loginState.value = LoginState.Error()
                }
            } catch (e: Exception) {
                _loginState.value = when (e) {
                    is FirebaseAuthInvalidUserException -> LoginState.Error()
                    is FirebaseAuthUserCollisionException -> LoginState.Error()
                    is CancellationException -> LoginState.Error()
                    else -> LoginState.Error()
                }
            }
        }
    }

    fun handleEmailPasswordLogin(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _loginState.value = LoginState.Loading
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                _loginState.value = if (result.user != null) {
                    LoginState.Success()
                } else {
                    LoginState.Error()
                }
            } catch (e: Exception) {
                _loginState.value = when (e) {
                    is FirebaseAuthInvalidUserException -> LoginState.Error()
                    is FirebaseAuthInvalidCredentialsException -> LoginState.Error()
                    else -> LoginState.Error()
                }
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}