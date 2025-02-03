package com.example.mony.feature.notas

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mony.feature.notas.classe.NotaItem

@Composable
fun NotasItem(
    note: NotaItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    isSelected: Boolean
) {
    // Definindo animações para a mudança de estado de seleção
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Color.LightGray else Color.White
    )

    val alpha by animateFloatAsState(
        targetValue = if (isSelected) 0.9f else 1f,
        animationSpec = tween(durationMillis = 200)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .clickable(onClick = onClick)
            .background(backgroundColor) // Usando animação para o fundo
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongClick() } // Lida com o clique longo
                )
            }
            .graphicsLayer(alpha = alpha), // Aplicando a animação de opacidade
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = note.title,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = note.content,
                maxLines = 2,
                color = Color.DarkGray,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotaItemPreview() {
    NotasItem(
        note = NotaItem(
            title = "Título da Nota",
            content = "Este é o conteúdo da nota. Ele pode ser um texto longo que será truncado se exceder o limite de linhas.",
        ),
        onClick = {},
        isSelected = false,
        onLongClick = {}
    )
}