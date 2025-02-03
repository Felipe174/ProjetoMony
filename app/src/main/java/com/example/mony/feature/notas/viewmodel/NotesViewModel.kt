package com.example.mony.feature.notas.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.mony.feature.notas.classe.NotaItem
import com.google.android.gms.tasks.Tasks
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

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadNotes()
        }
    }

    fun addNote(note: NotaItem) {
        viewModelScope.launch {
            try {
                val addedNoteId = addNoteToFirestore(note)
                val updatedNote = note.copy(id = addedNoteId)
                _notes.update { it + updatedNote }
            } catch (e: Exception) {
                Log.e("NotesViewModel", "Erro ao adicionar nota: ${e.message}")
                // Notifique o usuário sobre o erro
            }
        }
    }

    fun deleteNote(note: NotaItem) {
        viewModelScope.launch {
            try {
                deleteNoteFromFirestore(note)
                _notes.update { it.filter { it.id != note.id } }
            } catch (e: Exception) {
                Log.e("NotesViewModel", "Erro ao excluir nota: ${e.message}")
                // Notifique o usuário sobre o erro
            }
        }
    }

    fun updateNote(note: NotaItem) {
        viewModelScope.launch {
            try {
                updateNoteInFirestore(note)
                _notes.update { it.map { if (it.id == note.id) note else it } }
            } catch (e: Exception) {
                Log.e("NotesViewModel", "Erro ao atualizar nota: ${e.message}")
                // Notifique o usuário sobre o erro
            }
        }
    }

    private fun loadNotes() {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("notes")
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

    private suspend fun addNoteToFirestore(note: NotaItem): String {
        val firestore = FirebaseFirestore.getInstance()
        return try {
            val documentRef = firestore.collection("notes").add(note).await()
            Log.d("NotesViewModel", "Nota adicionada com sucesso: ${documentRef.id}")
            documentRef.id
        } catch (e: Exception) {
            Log.e("NotesViewModel", "Erro ao adicionar nota ao Firestore: ${e.message}")
            throw e // Consider showing user-friendly error message
        }
    }

    private suspend fun deleteNoteFromFirestore(note: NotaItem) {
        val firestore = FirebaseFirestore.getInstance()
        if (note.id.isNotEmpty()) {
            try {
                firestore.collection("notes").document(note.id).delete().await()
                Log.d("NotesViewModel", "Nota excluída com sucesso: ${note.id}")
            } catch (e: Exception) {
                Log.e("NotesViewModel", "Erro ao excluir nota do Firestore: ${e.message}")
                throw e // Consider showing user-friendly error message
            }
        } else {
            Log.e("NotesViewModel", "ID da nota está vazio, não é possível excluir.")
        }
    }

    private suspend fun updateNoteInFirestore(note: NotaItem) {
        val firestore = FirebaseFirestore.getInstance()
        if (note.id.isNotEmpty ()) {
            try {
                firestore.collection("notes").document(note.id).set(note).await()
                Log.d("NotesViewModel", "Nota atualizada com sucesso: ${note.id}")
            } catch (e: Exception) {
                Log.e("NotesViewModel", "Erro ao atualizar nota no Firestore: ${e.message}")
                throw e
            }
        } else {
            Log.e("NotesViewModel", "ID da nota está vazio, não é possível atualizar.")
        }
    }
}