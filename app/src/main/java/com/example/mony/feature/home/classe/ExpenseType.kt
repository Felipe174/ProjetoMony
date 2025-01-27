package com.example.mony.feature.home.classe

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mony.R


fun exampleExpenseTypes(): List<ExpenseType> {
    return listOf(
        ExpenseType(imageResId = R.drawable.prato, name = "Alimentação", isGain = false),
        ExpenseType(imageResId = R.drawable.onibus, name = "Transporte", isGain = false),
        ExpenseType(imageResId = R.drawable.vencimento, name = "Salário", isGain = true),
        ExpenseType(imageResId = R.drawable.coquetel, name = "Lazer", isGain = false),
        ExpenseType(imageResId = R.drawable.conta, name = "Conta", isGain = false)
    )
}

@Composable
fun TypeSelectionDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onSelect: (ExpenseType) -> Unit
) {
    val expenseTypes: List<ExpenseType> = exampleExpenseTypes()

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Selecione o Tipo") },
            text = {
                Box(modifier = Modifier.heightIn(max = 300.dp)) {
                    LazyColumn {
                        items(expenseTypes) { expenseType ->
                            val safeImageResId = if (expenseType.imageResId != 0) expenseType.imageResId else R.drawable.conta

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSelect(expenseType)
                                        onDismiss()
                                    }
                                    .padding(16.dp)
                                    .background(Color.LightGray.copy(alpha = 0.2f)), // Destaque na seleção
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = safeImageResId),
                                    contentDescription = expenseType.name,
                                    modifier = Modifier.size(30.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))

                                Text(expenseType.name)

                                Spacer(modifier = Modifier.weight(1f))

                                Text(
                                    text = if (expenseType.isGain) "Ganho" else "Gasto",
                                    color = if (expenseType.isGain) Color.Green else Color.Red
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TypeSelectionDialogPreview() {
    TypeSelectionDialog(
        showDialog = true,
        onDismiss = { /* No-op */ },
        onSelect = { /* No-op */ }
    )
}
