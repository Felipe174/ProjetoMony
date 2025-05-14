package com.example.mony.feature.conta.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(navController: NavController) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopAppBar(
                title = { Text("Ajuda", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("mais")}) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBackIosNew,
                            contentDescription = "Pesquisar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.onPrimary)
            )
            Surface(
                modifier = Modifier
                    .fillMaxSize(),
                color = Color.White
            ){

            //Introdução

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Card(
                        elevation = CardDefaults.cardElevation(1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        colors = CardDefaults.cardColors(Color.White)
                    ) {
                        Text(
                            text = "Precisa de ajuda? Estamos aqui para você!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.Start)
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = "No Mony, queremos garantir que você tenha a melhor experiência possível ao gerenciar suas finanças. Caso tenha dúvidas ou encontre algum problema, aqui estão algumas formas de obter suporte:",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.Start)
                        )
                    }

                    //Perguntas Frequentes

                    Card(
                        elevation = CardDefaults.cardElevation(1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, start = 10.dp, end = 10.dp, bottom = 10.dp),
                        colors = CardDefaults.cardColors(Color.White)

                    ) {
                        Text(
                            text = "\uD83D\uDCCB Perguntas Frequentes (FAQ)",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.Start)
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = "Antes de mais nada, confira nossa seção de perguntas frequentes. Lá você encontra respostas rápidas para as dúvidas mais comuns, como:\n" +
                                    "\n" +
                                    "Como registrar uma despesa ou receita?\n" +
                                    "\n"+
                                    "Como criar um orçamento?\n" +
                                    "\n"+
                                    "Como visualizar relatórios financeiros?\n" +
                                    "\n"+
                                    "O que fazer se esquecer minha senha?" +"\n",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.Start)
                        )
                    }

                    //Contato
                    
                    Card(
                        elevation = CardDefaults.cardElevation(1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp, bottom = 15.dp),
                        colors = CardDefaults.cardColors(Color.White)
                    ) {
                        Text(
                            text = "\uD83D\uDCE7 Entre em Contato",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.Start)
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = "Se não encontrar o que precisa no FAQ, você pode entrar em contato com nossa equipe de suporte:\n" +
                                    "\n" +
                                    "E-mail: suporte@mony.com\n" +
                                    "\n" +
                                    "Horário de Atendimento: Segunda a Sexta, das 9h às 18h" + "\n"
                                    +"Estamos comprometidos em fazer do Mony o seu principal aliado na organização financeira. Qualquer dúvida, não hesite em nos procurar!\n" +
                                    "\n" +
                                    "Equipe Mony" +"\n",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.Start)
                        )
                    }

                }
            }
        }
    }

@Preview
@Composable
fun HelpScreenPreview() {
    val navController = rememberNavController()
    HelpScreen(navController)
}