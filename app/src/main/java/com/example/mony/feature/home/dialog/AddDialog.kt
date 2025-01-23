package com.example.mony.feature.home.dialog

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mony.feature.home.classe.Expense
import com.example.mony.feature.home.classe.ExpenseType
import com.example.mony.feature.home.classe.TypeSelectionDialog
import com.example.mony.feature.home.classe.exampleExpenseTypes
import com.example.mony.feature.home.viewmodel.HomeViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onAdd: (Double, ExpenseType, String, String) -> Unit,
    homeViewModel: HomeViewModel,
    context: Context = LocalContext.current
) {
    var newExpenseAmount by rememberSaveable { mutableStateOf("") }
    var selectedType by rememberSaveable { mutableStateOf<ExpenseType?>(null) }
    var showTypeDialog by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf("") }
    var selectedDate by rememberSaveable { mutableStateOf("Selecionar Data") }

    // Função para resetar os campos
    fun resetFields() {
        newExpenseAmount = ""
        selectedType = null
        selectedDate = "Selecionar Data"
        errorMessage = ""
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                resetFields()
                onDismiss()
            },
            title = { Text(text = "Adicionar Transação", fontSize = 20.sp) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Botão segmentado para tipo de transação
                    var selectedIndex by remember { mutableStateOf(0) }
                    val options = listOf("Ganho", "Gasto")
                    val expenseTypes = exampleExpenseTypes()

                    SingleChoiceSegmentedButton(
                        options = options,
                        selectedIndex = selectedIndex,
                        onOptionSelected = { index ->
                            selectedIndex = index
                            selectedType = expenseTypes.find { it.name == options[index] }
                        }
                    )

                    Spacer(modifier = Modifier.height(7.dp))

                    Card() {
                        // Campo de valor
                        TextField(
                            value = newExpenseAmount,
                            onValueChange = { newExpenseAmount = it },
                            label = { Text("Valor (€)") },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botão de seleção de categoria
                    FilledTonalButton(
                        onClick = { showTypeDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Categoria: ${selectedType?.name ?: "Selecione Aqui"}")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Campo de data
                    FilledTonalButton(
                        onClick = {
                            val calendar = Calendar.getInstance()
                            DatePickerDialog(
                                context,
                                { _, selectedYear, selectedMonth, selectedDay ->
                                    selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Data: $selectedDate")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (errorMessage.isNotEmpty()) {
                        Text(text = errorMessage, color = Color.Red)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = newExpenseAmount.toDoubleOrNull()
                        if (amount != null && selectedType != null && selectedDate != "Selecionar Data") {
                            val finalAmount = if (selectedType?.name == "Gasto") -amount else amount
                            val expense = Expense(
                                id = "",
                                date = System.currentTimeMillis(),
                                amount = finalAmount,
                                type = selectedType!!
                            )
                            homeViewModel.addExpense(expense) // Função delegada ao ViewModel
                            resetFields()
                            onDismiss()
                        } else {
                            errorMessage = "Por favor, preencha todos os campos corretamente."
                        }
                    }
                ) {
                    Text("Adicionar")
                }
            },
            dismissButton = {
                Button(onClick = {
                    resetFields()
                    onDismiss()
                }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo de seleção de tipo (Categoria)
    TypeSelectionDialog(
        showDialog = showTypeDialog,
        onDismiss = { showTypeDialog = false },
        onSelect = { type ->
            selectedType = type
            showTypeDialog = false
        }
    )
}

@Composable
fun SingleChoiceSegmentedButton(
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        options.forEachIndexed { index, label ->
            val backgroundColor = when (label) {
                "Ganho" -> if (index == selectedIndex) Color(0xFF4CAF50) else Color(0xFFE8F5E9) // Verde
                "Gasto" -> if (index == selectedIndex) Color(0xFFF44336) else Color(0xFFFFEBEE) // Vermelho
                else -> Color.LightGray
            }
            val contentColor = if (index == selectedIndex) Color.White else Color.Black

            SegmentedButton(
                onClick = { onOptionSelected(index) },
                selected = index == selectedIndex,
                label = { Text(label) },
                modifier = Modifier.weight(1f),
                backgroundColor = backgroundColor,
                contentColor = contentColor
            )
        }
    }
}

@Composable
fun SegmentedButton(
    onClick: () -> Unit,
    selected: Boolean,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    contentColor: Color
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor),
        modifier = modifier
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            label()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddDialogPreview() {
    var showDialog by remember { mutableStateOf(true) }
    AddDialog(
        showDialog = showDialog,
        onDismiss = { showDialog = false },
        onAdd = { _, _, _, _ -> },
        homeViewModel = HomeViewModel(),
        context = LocalContext.current // Para execução, remova no preview real
    )
}