package com.example.mony.feature.home.dialog

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mony.R
import com.example.mony.feature.home.classe.Expense
import com.example.mony.feature.home.classe.TransactionType
import com.example.mony.feature.home.classe.TypeSelectionDialog
import com.example.mony.feature.home.viewmodel.HomeViewModel
import com.example.mony.ui.theme.GreenLight
import com.example.mony.ui.theme.RedLight
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onAdd: (Double, TransactionType, Long) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<TransactionType?>(null) }
    var selectedDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var showTypeDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Adicionar Transação") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AmountInput(
                        value = amount,
                        onValueChange = { amount = it }
                    )

                    TypeSelector(
                        selectedType = selectedType,
                        onSelectClick = { showTypeDialog = true }
                    )

                    DateSelector(
                        selectedDate = selectedDate,
                        formatter = dateFormatter,
                        onSelectClick = { showDatePicker = true }
                    )
                }
            },
            confirmButton = {
                ConfirmButton(
                    enabled = selectedType != null && amount.isNotBlank(),
                    onConfirm = {
                        onAdd(
                            amount.toDoubleOrNull() ?: 0.0,
                            selectedType ?: TransactionType.BILLS,
                            selectedDate
                        )
                        onDismiss()
                    }
                )
            },
            dismissButton = {
                DismissButton(onDismiss = onDismiss)
            }
        )
    }

    if (showTypeDialog) {
        TypeSelectionDialog(
            showDialog = true,
            onDismiss = { showTypeDialog = false },
            onSelect = { selectedType = it }
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate
        )

        CustomDatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            selectedDate = it
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false }
                ) {
                    Text("Cancelar")
                }
            },
            content = { // Nome do parâmetro corrigido
                DatePicker(
                    state = datePickerState
                )
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit // Renomear para content
) {
    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        content = content
    )
}

@Composable
private fun AmountInput(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) {
                onValueChange(newValue)
            }
        },
        label = { Text("") },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        modifier = Modifier.fillMaxWidth(),
        prefix = { Text("R$") }
    )
}

@Composable
private fun TypeSelector(
    selectedType: TransactionType?,
    onSelectClick: () -> Unit
) {
    FilledTonalButton(
        onClick = onSelectClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Text(
            text = selectedType?.let { stringResource(it.labelRes) } ?: "Selecione o tipo",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun DateSelector(
    selectedDate: Long,
    formatter: SimpleDateFormat,
    onSelectClick: () -> Unit
) {
    FilledTonalButton(
        onClick = onSelectClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = "Selecione a data"
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Data: ${formatter.format(Date(selectedDate))}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ConfirmButton(
    enabled: Boolean,
    onConfirm: () -> Unit
) {
    Button(
        onClick = onConfirm,
        enabled = enabled,
        modifier = Modifier.padding(8.dp)
    ) {
        Text("Adicionar")
    }
}


@Composable
private fun DismissButton(
    onDismiss: () -> Unit
) {
    TextButton(
        onClick = onDismiss,
        modifier = Modifier.padding(8.dp)
    ) {
        Text(  "Cancelar")
    }
}

@Preview(showBackground = true)
@Composable
fun AddDialogPreview() {
    var showDialog by remember { mutableStateOf(true) }
    AddDialog(
        showDialog = showDialog,
        onDismiss = { showDialog = false },
        onAdd = { _, _, _ -> }
    )
}