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

    fun updateNote(index: Int, newTitle: String, newContent: String) {
        if (index in _notes.indices) {
            _notes[index] = _notes[index].copy(title = newTitle, content = newContent)
        }
    }
}