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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mony.R
import com.example.mony.ui.theme.GreenLight
import com.example.mony.ui.theme.RedLight
import com.example.mony.ui.theme.White

enum class TransactionType(
    @DrawableRes val iconRes: Int,
    @StringRes val labelRes: Int,
    val isIncome: Boolean
) {
    FOOD(
        R.drawable.prato,
        R.string.type_food,
        isIncome = false
    ),
    TRANSPORT(
        R.drawable.onibus,
        R.string.type_transport,
        isIncome = false
    ),
    SALARY(
        R.drawable.salario,
        R.string.type_salary,
        isIncome = true
    ),
    ENTERTAINMENT(
        R.drawable.coquetel,
        R.string.type_entertainment,
        isIncome = false
    ),
    BILLS(
        R.drawable.conta,
        R.string.type_bills,
        isIncome = false
    );

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
        onSelect: (TransactionType) -> Unit
    ) {
        if (showDialog) {
            AlertDialog(
                onDismissRequest = onDismiss,
                containerColor = White,
                title = {
                    Text(
                        text = "Selecione o tipo",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                text = {
                    LazyColumn(
                        modifier = Modifier
                            .heightIn(max = 400.dp)
                            .fillMaxWidth()

                    ) {
                        items(TransactionType.entries) { type ->
                            TransactionTypeItem(
                                type = type,
                                onClick = {
                                    onSelect(type)
                                    onDismiss()
                                }
                            )
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
            targetValue = if (type.isIncome) {
                GreenLight.copy(alpha = 0.1f)
            } else {
                RedLight.copy(alpha = 0.1f)
            },
            animationSpec = tween(200)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(White)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = type.iconRes),
                contentDescription = stringResource(id = type.labelRes),
                tint = if (type.isIncome) {
                    MaterialTheme.colorScheme.tertiary
                } else {
                    MaterialTheme.colorScheme.error
                },
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
                        id = if (type.isIncome) {
                            R.string.type_income
                        } else {
                            R.string.type_expense
                        }
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Selecione",
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }

@Preview
@Composable
fun TypeSelectionDialogPreview() {
    TypeSelectionDialog(
        showDialog = true,
        onDismiss = {},
        onSelect = {}
    )
}
