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
import kotlinx.coroutines.launch

open class NotesViewModel : ViewModel() {

    private val _notes = MutableStateFlow<List<NotaItem>>(emptyList())
    val notes: StateFlow<List<NotaItem>> = _notes

    init {
        viewModelScope.launch {
            try {
                loadNotes()
            } catch (e: Exception) {
                Log.e("NotesViewModel", "Erro ao carregar notas no init: ${e.message}")
            }
        }
    }

    open fun addNote(note: NotaItem) {
        viewModelScope.launch(Dispatchers.IO) {
            _notes.value += note // Atualiza localmente
            try {
                val addedNoteId = addNoteToFirestore(note) // Adiciona ao Firestore
                _notes.value = _notes.value.map {
                    if (it.id == "") it.copy(id = addedNoteId) else it
                }
            } catch (e: Exception) {
                _notes.value -= note // Reverte em caso de erro
            }
        }
    }

    fun loadNotes() {
        viewModelScope.launch(Dispatchers.IO) { // Mover para uma thread de fundo
            try {
                val fetchedNotes = getNotesFromFirestore()
                Log.d("NotesViewModel", "Notas carregadas: $fetchedNotes")
                _notes.value = fetchedNotes
            } catch (e: Exception) {
                Log.e("NotesViewModel", "Erro ao carregar notas: ${e.message}")
            }
        }
    }

    open fun deleteNote(note: NotaItem) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                deleteNoteFromFirestore(note) // Remove do Firestore
                _notes.value = _notes.value.filter { it.id != note.id } // Atualiza a lista local
            } catch (e: Exception) {
                Log.e("NotesViewModel", "Erro ao excluir nota: ${e.message}")
            }
        }
    }

    open fun updateNote(note: NotaItem) {
        viewModelScope.launch(Dispatchers.IO) { // Certifique-se de que está na thread de fundo
            try {
                updateNoteInFirestore(note) // Atualiza no Firestore
                _notes.value = _notes.value.map { if (it.id == note.id) note else it } // Atualiza o estado local
                loadNotes()
            } catch (e: Exception) {
                Log.e("NotesViewModel", "Erro ao atualizar nota: ${e.message}")
            }
        }
    }



    // Funções privadas
    private suspend fun addNoteToFirestore(note: NotaItem): String {
         val firestore = FirebaseFirestore.getInstance()
        return try {
            val documentRef = Tasks.await(firestore.collection("notes").add(note))
            Log.d("NotesViewModel", "Nota adicionada com sucesso: ${documentRef.id}")
            documentRef.id // Retorna o ID do documento criado
        } catch (e: Exception) {
            Log.e("NotesViewModel", "Erro ao adicionar nota ao Firestore: ${e.message}")
            throw e
        }
    }

    private suspend fun getNotesFromFirestore(): List<NotaItem> {
        val firestore = FirebaseFirestore.getInstance()
        return try {
            val snapshot = Tasks.await(firestore.collection("notes").get())
            snapshot.documents.map { document ->
                document.toObject(NotaItem::class.java)?.copy(id = document.id) ?: NotaItem()
            }
        } catch (e: Exception) {
            Log.e("NotesViewModel", "Erro ao buscar notas do Firestore: ${e.message}")
            emptyList()
        }
    }

    private suspend fun deleteNoteFromFirestore(note: NotaItem) {
         val firestore = FirebaseFirestore.getInstance()
        if (note.id.isNotEmpty()) {
            try {
                Tasks.await(firestore.collection("notes").document(note.id).delete())
                Log.d("NotesViewModel", "Nota excluída com sucesso: ${note.id}")
            } catch (e: Exception) {
                Log.e("NotesViewModel", "Erro ao excluir nota do Firestore: ${e.message}")
                throw e
            }
        } else {
            Log.e("NotesViewModel", "ID da nota está vazio, não é possível excluir.")
        }
    }

    private suspend fun updateNoteInFirestore(note: NotaItem) {
         val firestore = FirebaseFirestore.getInstance()
        if (note.id.isNotEmpty()) {
            try {
                Tasks.await(firestore.collection("notes").document(note.id).set(note))
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

