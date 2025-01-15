package com.example.mony.feature.notas.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.mony.feature.notas.NotasItem

class NotesViewModel : ViewModel() {
    private val _notes = mutableStateListOf<NotasItem>() // Lista observável
    val notes: List<NotasItem> get() = _notes

    fun addNote(title: String, content: String) {
        _notes.add(NotasItem(title = title, content = content))
    }

    fun updateNote(index: Int, title: String, content: String) {
        if (index in _notes.indices) { // Certifique-se de que o índice é válido
            _notes[index] = NotasItem(title, content)
        } else {
            throw IndexOutOfBoundsException("Índice $index fora do intervalo.")
        }
    }
}
