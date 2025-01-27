

package com.example.mony.feature.conta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mony.R
import com.example.mony.feature.utils.AppState
import com.example.mony.feature.utils.navegation.MyApp
import com.example.mony.feature.utils.navegation.topLevelDestinations
import com.google.firebase.auth.FirebaseAuth

class ContaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContaScreen(appState: AppState,navController: NavController, onLogout: () -> Unit) {

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            topLevelDestinations.forEach { destination ->
                val selected = appState.isRouteInHierarchy(
                    destination.route
                )
                item(
                    selected = selected,
                    icon = {
                        Icon(
                            imageVector = if (selected) {
                                destination.selectedIcon
                            } else {
                                destination.unselectedIcon
                            },
                            contentDescription = stringResource(destination.iconTextId),
                            tint = if (selected) {
                                destination.selectedIconColor
                            } else {
                                destination.unselectedIconColor
                            }
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(destination.iconTextId),
                            maxLines = 1
                        )
                    },
                    onClick = { appState.navigateToTopLevelDestination(destination.route) }
                )
            }
        }
    ) {

        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(color = Color(android.graphics.Color.parseColor("#F2F1F6"))),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileHeader(navController)

            Spacer(modifier = Modifier.height(16.dp))

            // Nome e e-mail
            Text(
                text = "Usuario",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 35.dp),
                color = Color(android.graphics.Color.parseColor("#32357A"))
            )
            Text(
                text = "a35311@gmail.com",
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 5.dp),
                color = Color(android.graphics.Color.parseColor("#747679"))
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text("Configuração da Conta"
                ,fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color= Color(android.graphics.Color.parseColor("#808080")),
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 10.dp,top=10.dp,bottom=10.dp)
            )

            // Menu de opções com navegação
            MenuItem(icon = R.drawable.ic_user, title = "Informações da Conta") {
                navController.navigate("info")
            }
            MenuItem(icon = R.drawable.escudo, title = "Segurança e Senha") {
                navController.navigate("secure")
            }

            Text("Outros"
                ,fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color= Color(android.graphics.Color.parseColor("#808080")),
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 10.dp, top = 10.dp,bottom=10.dp)
            )

            MenuItem(icon = R.drawable.ajuda, title = "Ajuda") {
                navController.navigate("help")
            }
            MenuItem(icon = R.drawable.more, title = "Sobre Nós") {
                navController.navigate("about")
            }
            MenuItem(icon = R.drawable.sair, title = "Logout", onClick = { onLogout() })
        }
        }
    }

    @Composable
    fun ProfileHeader(navController: NavController) {
        ConstraintLayout(
            Modifier
                .height(250.dp)
                .fillMaxWidth()
        ) {
            val (background, profile, title, back, pen) = createRefs()

            // Fundo com bordas arredondadas na parte inferior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(
                        RoundedCornerShape(
                            bottomStart = 32.dp,
                            bottomEnd = 32.dp
                        ) // Apenas as bordas inferiores são arredondadas
                    )
                    .background(
                        Brush.linearGradient(
                            colors = listOf( // roxo
                                Color(android.graphics.Color.parseColor("#9932CC")),
                                Color(android.graphics.Color.parseColor("#4A4D9D"))
                            )
                        )
                    )
                    .constrainAs(background) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            // Imagem do perfil
            Image(
                painter = painterResource(id = R.drawable.user),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .constrainAs(profile) {
                        top.linkTo(background.top, margin = (200).dp)
                        start.linkTo(parent.start, margin = 16.dp)
                        end.linkTo(parent.end, margin = 16.dp)
                        bottom.linkTo(background.bottom, margin = 16.dp)
                    },
                contentScale = ContentScale.Crop
            )

            // Botão de voltar
            IconButton(
                onClick = { navController.navigate("home") },
                modifier = Modifier.constrainAs(back) {
                    top.linkTo(background.top, margin = 16.dp)
                    start.linkTo(parent.start, margin = 5.dp)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Voltar",
                    modifier = Modifier.size(26.dp),
                    tint = Color.White
                )
            }

            // Botão de editar
            IconButton(
                onClick = { navController.navigate("info") },
                modifier = Modifier.constrainAs(pen) {
                    top.linkTo(background.top, margin = 110.dp)
                    end.linkTo(background.end, margin = 110.dp)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.edit),
                    modifier = Modifier.size(24.dp),
                    contentDescription = "Editar",
                    tint = Color.White,
                )
            }
        }
    }
@Composable
fun MenuItem(icon: Int, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier
                .size(25.dp)
                .align(Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            modifier = Modifier.align(Alignment.CenterVertically)
            ,fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Image(
                painter = painterResource(id = R.drawable.next),
                contentDescription = null,
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Gray),
                modifier = Modifier
                    .size(10.dp)
            )
        }
    }
}

fun onLogout() {
    // Chama o método de logout do Firebase
    FirebaseAuth.getInstance().signOut()
}

@Preview(showBackground = true)
@Composable
fun ContaScreenPreview() {
    // Usando um NavController simples
    val navController = rememberNavController()
    // Criando uma instância do AppState
    val appState = AppState(navController)

    ContaScreen(appState = appState, navController = navController , onLogout = {})
}
