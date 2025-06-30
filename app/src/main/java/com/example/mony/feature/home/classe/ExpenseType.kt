package com.example.mony.feature.home.classe

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mony.R
import com.example.mony.ui.theme.GreenLight
import com.example.mony.ui.theme.MonyTheme
import com.example.mony.ui.theme.RedLight

enum class TransactionType(
    @DrawableRes val iconRes: Int,
    @StringRes val labelRes: Int,
    val isIncome: Boolean
) {
    FOOD(R.drawable.prato, R.string.type_food, false),
    TRANSPORT(R.drawable.onibus, R.string.type_transport, false),
    SALARY(R.drawable.salario, R.string.type_salary, true),
    ENTERTAINMENT(R.drawable.coquetel, R.string.type_entertainment, false),
    BILLS(R.drawable.conta, R.string.type_bills, false);

    companion object {
        fun fromName(name: String?): TransactionType {
            return entries.find { it.name == name } ?: BILLS
        }
    }
}

@Composable
fun TypeSelectionDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onSelect: (TransactionType) -> Unit,
    onCreateCustom: () -> Unit = {}
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = MaterialTheme.colorScheme.background,
            title = {
                Text(
                    text = "Selecione o tipo",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .heightIn(max = 500.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background),
                ) {
                    // Seção de Ganhos
                    item { SectionHeader("Ganhos") }
                    items(TransactionType.entries.filter { it.isIncome }) { type ->
                        TransactionTypeItem(
                            type = type,
                            onClick = {
                                onSelect(type)
                                onDismiss()
                            }
                        )
                    }

                    // Seção de Despesas
                    item { SectionHeader("Despesas") }
                    items(TransactionType.entries.filter { !it.isIncome }) { type ->
                        TransactionTypeItem(
                            type = type,
                            onClick = {
                                onSelect(type)
                                onDismiss()
                            }
                        )
                    }

                    // Seção de Personalizado
                    item { SectionHeader("Personalizado") }
                    item {
                        CustomTypeAddButton(onClick = {
                            onDismiss()
                            onCreateCustom()
                        })
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun TransactionTypeItem(
    type: TransactionType,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (type.isIncome)
            GreenLight.copy(alpha = 0.1f)
        else
            RedLight.copy(alpha = 0.1f),
        animationSpec = tween(200)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(backgroundColor, shape = MaterialTheme.shapes.medium)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = type.iconRes),
            contentDescription = stringResource(id = type.labelRes),
            tint = if (type.isIncome)
                Color.Green
            else
                Color.Red,
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(id = type.labelRes),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(
                    if (type.isIncome) R.string.type_income else R.string.type_expense
                ),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Selecionar tipo",
            tint = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 8.dp, horizontal = 16.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun CustomTypeAddButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Criar novo tipo",
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Criar novo tipo personalizado",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview
@Composable
fun TypeSelectionDialogPreview() {
    MonyTheme(darkTheme = true) {
    TypeSelectionDialog(
        showDialog = true,
        onDismiss = {},
        onSelect = {},
        onCreateCustom = {
            // Navegar ou abrir nova tela para criar tipo personalizado
        }
    )
}}
