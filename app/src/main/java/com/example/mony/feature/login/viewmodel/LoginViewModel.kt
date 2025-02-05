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
    data class Success(val userEmail: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

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
                    _loginState.value = LoginState.Success(email)
                } ?: run {
                    _loginState.value = LoginState.Idle
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Erro na verificação de autenticação: ${e.message ?: "Desconhecido"}")
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
                    _loginState.value = LoginState.Success(user.email ?: "Usuário sem e-mail")
                } ?: run {
                    _loginState.value = LoginState.Error("Falha ao autenticar usuário")
                }
            } catch (e: Exception) {
                _loginState.value = when (e) {
                    is FirebaseAuthInvalidUserException -> LoginState.Error("Conta inválida")
                    is FirebaseAuthUserCollisionException -> LoginState.Error("Conta já vinculada")
                    is CancellationException -> LoginState.Error("Operação cancelada")
                    else -> LoginState.Error("Erro: ${e.message ?: "Falha na autenticação"}")
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
                    LoginState.Success(result.user?.email ?: "Usuário sem e-mail")
                } else {
                    LoginState.Error("Falha ao autenticar usuário")
                }
            } catch (e: Exception) {
                _loginState.value = when (e) {
                    is FirebaseAuthInvalidUserException -> LoginState.Error("Conta inválida")
                    is FirebaseAuthInvalidCredentialsException -> LoginState.Error("Credenciais inválidas")
                    else -> LoginState.Error("Erro: ${e.message ?: "Falha na autenticação"}")
                }
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}