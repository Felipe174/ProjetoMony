package com.example.mony.feature.notas.classe

data class Note(
    val id: Int, // ID único da nota
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis() // Timestamp de criação/modificação
)