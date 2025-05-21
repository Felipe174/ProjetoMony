package com.example.mony.feature.notas.classe

data class NotaItem(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    // Construtor sem argumentos (necessário para o Firebase funcionar)
    constructor() : this("", "", "", )
}