package com.example.mony.feature.home.classe

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mony.R


@Composable
fun ExpenseItem(
    expense: Expense,
    isSelected: Boolean,
    onSelect: (Boolean) -> Unit,
    onLongPress: () -> Unit // Adiciona um callback para o longo pressionar
) {
    var showCheckbox by remember { mutableStateOf(false) } // Estado para mostrar o checkbox

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        onLongPress() // Chama o callback para adicionar à seleção
                    }
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Exibir o Checkbox apenas se estiver selecionado
        if (isSelected) {
            Checkbox(
                checked = true,
                onCheckedChange = { onSelect(false) } // Desmarcar ao clicar
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Exibir a imagem do tipo de gasto ou ganho
        Image(
            painter = painterResource(id = expense.type.imageResId),
            contentDescription = expense.type.name,
            modifier = Modifier.size(25.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = expense.type.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "${if (expense.amount < 0) "-" else "+"}${Math.abs(expense.amount)}€",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (expense.amount < 0) Color.Red else Color.Green
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpenseItemPreview() {
    // Exemplo de ExpenseType
    val exampleType = ExpenseType(
        imageResId = R.drawable.onibus, // Substitua pelo seu recurso de imagem
        name = "Salário",
        isGain = true
    )

    // Exemplo de Expense
    val exampleExpense = Expense(
        id = "1",
        date = System.currentTimeMillis(),
        amount = 1500.0, // Exemplo de valor
        type = exampleType
    )

    // Exibir o ExpenseItem
    ExpenseItem(expense = exampleExpense, isSelected = false, onSelect = {}, onLongPress = {}) // Passa o objeto Expense
}