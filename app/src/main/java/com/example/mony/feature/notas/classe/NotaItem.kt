package com.example.mony.feature.notas.classe

data class NotaItem(
    val id: String = "",         // Valor padrão para garantir que o Firebase funcione
    val title: String = "",      // Valor padrão para garantir que o Firebase funcione
    val content: String = ""     // Valor padrão para garantir que o Firebase funcione
) {
    // Construtor sem argumentos (necessário para o Firebase funcionar)
    constructor() : this("", "", "")
}