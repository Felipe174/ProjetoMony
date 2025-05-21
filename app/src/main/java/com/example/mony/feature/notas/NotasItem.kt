package com.example.mony.feature.notas

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
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
import com.example.mony.ui.theme.MonyTheme
import com.example.mony.ui.theme.White

@Composable
fun NotasItem(
    note: NotaItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Color.LightGray else MaterialTheme.colorScheme.background
    )

    val alpha by animateFloatAsState(
        targetValue = if (isSelected) 0.9f else 1f,
        animationSpec = tween(durationMillis = 200)
    )

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(2.dp)
            .graphicsLayer(alpha = alpha)
            .background(backgroundColor)
            .combinedClickable(
                onClick = {
                    Log.d("NotasItem", "Clique simples: ${note.title}")
                    onClick()
                },
                onLongClick = {
                    Log.d("NotasItem", "Clique longo: ${note.title}")
                    onLongClick()
                }
            )
            .border(0.1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = note.title,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = note.content,
                maxLines = 1,
                color = MaterialTheme.colorScheme.secondary,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotaItemPreview() {

    MonyTheme {
    NotasItem(
        note = NotaItem(
            title = "Título da Nota",
            content = "Este é o conteúdo da nota. Ele pode ser um texto longo que será truncado se exceder o limite de linhas.",
        ),
        onClick = {},
        isSelected = false,
        onLongClick = {}
    )
}}