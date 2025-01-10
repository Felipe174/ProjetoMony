package com.example.mony.feature.notas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class NotasItem(
    val title: String,
    val content: String
)

@Composable
fun NotaItem(note: NotasItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = note.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis, // Para truncar o texto se for muito longo
              //style =
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = note.content,
                //style =
                maxLines = 2,
                overflow = TextOverflow.Ellipsis // Para truncar o texto se for muito longo
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotaItemPreview() {
    NotaItem(
        note = NotasItem(
            title = "Título da Nota",
            content = "Este é o conteúdo da nota. Ele pode ser um texto longo que será truncado se exceder o limite de linhas.",
            ),
        onClick = {}
    )
}