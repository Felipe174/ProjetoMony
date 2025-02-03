package com.example.mony.feature.login.viewmodel

import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val userEmail: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            checkCurrentUser()
        }
    }

    private fun checkCurrentUser() {
        auth.currentUser?.email?.let {
            _loginState.value = LoginState.Success(it)
        }
    }

    fun handleGoogleSignInResult(account: GoogleSignInAccount) {
        viewModelScope.launch(Dispatchers.IO) {
            _loginState.value = LoginState.Loading
            try {
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                val result = auth.signInWithCredential(credential).await()

                _loginState.value = if (result.user != null) {
                    LoginState.Success(result.user?.email ?: "Usuário sem e-mail")
                } else {
                    LoginState.Error("Falha ao obter usuário")
                }

            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.localizedMessage ?: "Erro desconhecido")
            }
        }
    }

    fun resetState() {
        _loginState.update { LoginState.Idle }
    }
}
