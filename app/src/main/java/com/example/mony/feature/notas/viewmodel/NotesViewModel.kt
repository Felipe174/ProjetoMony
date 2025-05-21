package com.example.mony.feature.notas.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mony.feature.notas.classe.NotaItem
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

open class NotesViewModel : ViewModel() {

    private val _notes = MutableStateFlow<List<NotaItem>>(emptyList())
    val notes: StateFlow<List<NotaItem>> = _notes

    private val auth: FirebaseAuth = Firebase.auth
    private val userId: String?
        get() = auth.currentUser?.uid

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadNotes()
        }
    }

    sealed interface UiState {
        object Idle : UiState
        object Loading : UiState
        data class Success(val message: String? = null) : UiState
        data class Error(val message: String) : UiState
    }

    fun addNote(note: NotaItem) {
        val uid = userId ?: return
        viewModelScope.launch {
            try {
                val addedNoteId = addNoteToFirestore(uid, note)
                Log.d("NotesViewModel", "Nota adicionada com sucesso: $addedNoteId")
                // Não adiciona manualmente à lista — espera o snapshotListener capturar a mudança
            } catch (e: Exception) {
                Log.e("NotesViewModel", "Erro ao adicionar nota: ${e.message}")
            }
        }
    }

    fun deleteNote(note: NotaItem) {
        val uid = userId ?: return
        viewModelScope.launch {
            try {
                deleteNoteFromFirestore(uid, note)
            } catch (e: Exception) {
                Log.e("NotesViewModel", "Erro ao excluir nota: ${e.message}")
            }
        }
    }

    fun updateNote(note: NotaItem) {
        val uid = userId ?: return
        viewModelScope.launch {
            try {
                updateNoteInFirestore(uid, note)
            } catch (e: Exception) {
                Log.e("NotesViewModel", "Erro ao atualizar nota: ${e.message}")
            }
        }
    }

    private fun loadNotes() {
        val uid = userId ?: return
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("users")
            .document(uid)
            .collection("notes")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("NotesViewModel", "Erro ao buscar notas: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val notesList = snapshot.documents.mapNotNull { document ->
                        document.toObject(NotaItem::class.java)?.copy(id = document.id)
                    }
                    _notes.value = notesList
                }
            }
    }

    private suspend fun addNoteToFirestore(uid: String, note: NotaItem): String {
        val firestore = FirebaseFirestore.getInstance()
        return try {
            val docRef = firestore.collection("users")
                .document(uid)
                .collection("notes")
                .add(note)
                .await()

            docRef.id
        } catch (e: Exception) {
            Log.e("NotesViewModel", "Erro ao adicionar nota: ${e.message}")
            throw e
        }
    }

    private suspend fun deleteNoteFromFirestore(uid: String, note: NotaItem) {
        if (note.id.isNotEmpty()) {
            val firestore = FirebaseFirestore.getInstance()
            try {
                firestore.collection("users")
                    .document(uid)
                    .collection("notes")
                    .document(note.id)
                    .delete()
                    .await()
            } catch (e: Exception) {
                Log.e("NotesViewModel", "Erro ao excluir nota: ${e.message}")
                throw e
            }
        }
    }

    private suspend fun updateNoteInFirestore(uid: String, note: NotaItem) {
        if (note.id.isNotEmpty()) {
            val firestore = FirebaseFirestore.getInstance()
            try {
                firestore.collection("users")
                    .document(uid)
                    .collection("notes")
                    .document(note.id)
                    .set(note)
                    .await()
            } catch (e: Exception) {
                Log.e("NotesViewModel", "Erro ao atualizar nota: ${e.message}")
                throw e
            }
        }
    }
}
