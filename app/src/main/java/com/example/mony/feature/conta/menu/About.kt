package com.example.mony.feature.conta.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mony.R
import com.example.mony.ui.theme.MonyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    // Usando Scaffold para incluir a TopAppBar corretamente
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sobre", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("mais") }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBackIosNew,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.onPrimary)
            )
        }
    ) { innerPadding ->
        // LazyColumn com o padding do Scaffold aplicado corretamente
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Isso corrige o padding
                .background(Color.White)
        ) {

            item {
                    SobreText(navController) // Chamando o conteúdo da tela
            }

            // Itens adicionais (como o MenuItemSobre)
            item {
                Card(
                    elevation = CardDefaults.cardElevation(1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start=10.dp, end=10.dp, bottom = 5.dp),
                    colors = CardDefaults.cardColors(Color.White)
                ) {
                    MenuItemSobre(
                        title = "Termos de Uso",
                        onClick = { navController.navigate("TermosDeUso") }
                    )
                }
            }
            item {
                Card(
                    elevation = CardDefaults.cardElevation(1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start=10.dp, end=10.dp, bottom = 5.dp),
                    colors = CardDefaults.cardColors(Color.White)
                ) {

                    MenuItemSobre(
                        title = "Privacidade",
                        onClick = { navController.navigate("Privacidade") }
                    )
                }
            }
            item {
                Card(
                    elevation = CardDefaults.cardElevation(1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start=10.dp, end=10.dp, bottom = 5.dp),
                    colors = CardDefaults.cardColors(Color.White)
                ) {
                    MenuItemSobre(
                        title = "Licenças",
                        onClick = { navController.navigate("Licencas") }
                    )
                }
            }
            item {
                Card(
                    elevation = CardDefaults.cardElevation(1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start=10.dp, end=10.dp, bottom = 5.dp),
                    colors = CardDefaults.cardColors(Color.White)
                ) {
                    MenuItemSobre(
                        title = "Agradecimentos",
                        onClick = { navController.navigate("Agradecimentos") }
                    )
                }
            }
        }
    }
}

@Composable
fun SobreText(navController: NavController) {
    // Definindo o conteúdo da seção "Sobre Nós"
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top=10.dp, start=10.dp, end=10.dp, bottom = 15.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = "Sobre Nós",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = """
                    Bem-vindo ao Mony! Somos uma aplicação focada em ajudar você a gerenciar seu dinheiro de forma simples, eficiente e inteligente.

                    Nosso objetivo é empoderar pessoas a terem maior controle sobre suas finanças, alcançando seus objetivos financeiros e construindo uma relação mais saudável com o dinheiro. Sabemos que organizar as finanças pode ser desafiador, mas com as ferramentas certas, tudo se torna mais fácil!

                    O que oferecemos no Mony:
                    • Controle Financeiro Simplificado: Registre despesas, receitas e acompanhe seu saldo em tempo real.
                    • Planejamento Personalizado: Crie orçamentos e metas financeiras que se adaptam à sua realidade.
                    • Relatórios Visuais: Veja para onde está indo o seu dinheiro com gráficos claros e intuitivos.
                    • Segurança Garantida: Suas informações financeiras são protegidas com tecnologia de ponta.

                    Acreditamos que o primeiro passo para o sucesso financeiro é a organização, e o Mony está aqui para ser seu parceiro nessa jornada.

                    Seja para economizar, planejar um sonho ou simplesmente entender melhor suas finanças, conte conosco para fazer do gerenciamento de dinheiro uma tarefa descomplicada e prática.

                    Equipe Mony
                """.trimIndent(),
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.align(Alignment.Start)
            )
        }
    }
}

@Composable
fun MenuItemSobre(title: String, onClick: () -> Unit) {
    // Item de menu com uma linha clicável
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(10.dp)
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.align(Alignment.CenterVertically),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(id = R.drawable.next),
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(16.dp)
        )
    }
    Spacer(modifier = Modifier.height(10.dp))
}

@Preview
@Composable
fun AboutScreenPreview() {
    val navController = rememberNavController()
    MonyTheme {
    AboutScreen(navController)
}}
