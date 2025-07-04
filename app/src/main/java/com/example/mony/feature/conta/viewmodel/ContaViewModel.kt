package com.example.mony.feature.conta.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mony.feature.conta.classe.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


open class ContaViewModel : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    init {
        viewModelScope.launch {
            fetchUserProfile()
        }
    }

    private fun fetchUserProfile() {
        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        firebaseUser?.let { user ->
            val providerId = user.providerData
                .firstOrNull { it.providerId != "firebase" }?.providerId ?: ""

            _userProfile.update {
                UserProfile(
                    name = user.displayName ?: "Usuário",
                    email = user.email ?: "E-mail não disponível",
                    photoUrl = user.photoUrl?.toString(),
                    providerId = providerId
                )
            }
        }
    }
}

class FakeContaViewModel : ContaViewModel() {
    // Podes sobrescrever LiveData/StateFlow com valores fixos aqui se necessário
}


