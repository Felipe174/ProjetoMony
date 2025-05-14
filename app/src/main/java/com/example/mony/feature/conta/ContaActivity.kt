package com.example.mony.feature.conta

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.mony.R
import com.example.mony.feature.conta.classe.UserProfile
import com.example.mony.feature.conta.viewmodel.ContaViewModel
import com.example.mony.feature.home.viewmodel.HomeViewModel
import com.example.mony.feature.login.LoginActivity
import com.example.mony.feature.notas.viewmodel.NotesViewModel
import com.example.mony.feature.utils.AppState
import com.example.mony.feature.utils.navegation.MyApp
import com.example.mony.feature.utils.navegation.getTopLevelDestinations
import com.example.mony.ui.theme.Amarelo
import com.example.mony.ui.theme.AmareloMC
import com.example.mony.ui.theme.Black
import com.example.mony.ui.theme.MonyTheme
import com.example.mony.ui.theme.RedLight
import com.example.mony.ui.theme.Roxo
import com.example.mony.ui.theme.RoxoDark
import com.example.mony.ui.theme.RoxoMedio
import com.example.mony.ui.theme.White
import com.google.firebase.auth.FirebaseAuth

class ContaActivity : ComponentActivity() {
    private val notesViewModel: NotesViewModel by viewModels()
    private val contaViewModel: ContaViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MonyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    MyApp(
                        notesViewModel = notesViewModel,
                        contaViewModel = contaViewModel,
                        homeViewModel = homeViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun ContaScreen(appState: AppState, navController: NavController, contaViewModel: ContaViewModel, onLogout: () -> Unit) {
    val userProfile = contaViewModel.userProfile.collectAsState().value
    val context = LocalContext.current
    val topLevelDestinations = getTopLevelDestinations()



    NavigationSuiteScaffold(
        navigationSuiteItems = {
            topLevelDestinations.forEach { destination ->
                val selected = appState.isRouteInHierarchy(destination.route)

                item(
                    selected = selected,
                    icon = {
                        Icon(
                            imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                            contentDescription = stringResource(destination.iconTextId),
                            tint = if (selected) destination.selectedIconColor else destination.unselectedIconColor
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(destination.iconTextId),
                            maxLines = 1
                        )
                    },
                    onClick = {
                        appState.navigateToTopLevelDestination(destination.route)
                    }
                )
            }
        }
    ) {

        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileHeader(userProfile = userProfile,navController)

            Spacer(modifier = Modifier.height(16.dp))

            // Nome e e-mail
            Text(
                text = userProfile?.name ?: "Carregando...",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 35.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = userProfile?.email ?: "Carregando...",
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 5.dp),
                color = MaterialTheme.colorScheme.onSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
            ) {

                Text(
                    "Configuração da Conta", fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 10.dp, top = 10.dp, bottom = 10.dp)
                )

                // Menu de opções com navegação
                MenuItem(icon = R.drawable.ic_user, title = "Informações da Conta", onClick = {navController.navigate("info")},isLogout = false)

                Spacer(modifier = Modifier.height(5.dp))

                MenuItem(icon = R.drawable.escudo, title = "Segurança e Senha", onClick = { navController.navigate("secure")},isLogout = false)

            }


            Card(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
            ) {
                Text(
                    "Outros", fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 10.dp, top = 10.dp, bottom = 10.dp)
                )

                MenuItem(icon = R.drawable.ajuda, title = "Ajuda", onClick = {navController.navigate("help")},isLogout = false)

                Spacer(modifier = Modifier.height(5.dp))

                MenuItem(icon = R.drawable.more, title = "Sobre Nós", onClick = {navController.navigate("about")}, isLogout = false)

            }
            Spacer(modifier = Modifier.height(5.dp))

            Card(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
            ) {
                MenuItem(
                    icon = R.drawable.sair,
                    title = "Logout",
                    onClick = {
                        FirebaseAuth.getInstance().signOut()

                        Toast.makeText(context, "Desconectado com sucesso", Toast.LENGTH_SHORT).show()

                        val intent = Intent(context, LoginActivity::class.java)
                        context.startActivity(intent)
                        (context as? Activity)?.finish()
                    },
                    isLogout = true
                )

            }
        }
    }
}

@Composable
fun ProfileHeader(userProfile: UserProfile?, navController: NavController) {
    ConstraintLayout(
        Modifier
            .height(250.dp)
            .fillMaxWidth()
    ) {
        val (background, profile, title, back, pen) = createRefs()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(
                    RoundedCornerShape(
                        bottomStart = 32.dp,
                        bottomEnd = 32.dp
                    )
                )
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceContainerLow ,
                            MaterialTheme.colorScheme.surfaceContainerLowest,
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
        val imageModifier = Modifier
            .size(140.dp)
            .clip(CircleShape)
            .background(Color.White)
            .constrainAs(profile) {
                top.linkTo(background.top, margin = (200).dp)
                start.linkTo(parent.start, margin = 16.dp)
                end.linkTo(parent.end, margin = 16.dp)
                bottom.linkTo(background.bottom, margin = 16.dp)
            }

        if (userProfile?.photoUrl != null) {
            AsyncImage(
                model = userProfile.photoUrl,
                contentDescription = "Profile Image",
                modifier = imageModifier,
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.user), // Imagem padrão
                contentDescription = "Profile Image",
                modifier = imageModifier,
                contentScale = ContentScale.Crop
            )
        }

        // Botão de voltar
        IconButton(
            onClick = { navController.navigate("home") },
            modifier = Modifier.constrainAs(back) {
                top.linkTo(background.top, margin = 20.dp)
                start.linkTo(parent.start, margin = 5.dp)
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Voltar",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSecondary
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
                tint = MaterialTheme.colorScheme.onSecondary,
            )
        }
    }
}

@Composable
fun MenuItem(
    icon: Int,
    title: String,
    onClick: () -> Unit,
    isLogout: Boolean = false
) {
    Column {
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
                colorFilter = ColorFilter.tint(if (isLogout) Color.Red else MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .size(25.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSecondary
            )

            Image(
                painter = painterResource(id = R.drawable.next),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary),
                modifier = Modifier.size(10.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Divider(
            color = MaterialTheme.colorScheme.secondary,
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
    }
}
@Preview(showBackground = true)
@Composable
fun ContaScreenPreview() {
    // Usando um NavController simples
    val navController = rememberNavController()

    // Criando um ViewModel fictício (evitar usar ViewModels reais no preview)
    val contaViewModel = ContaViewModel()

    // Criando um AppState fictício
    val appState = remember { AppState(navController) }

    ContaScreen(
        appState = appState,
        navController = navController,
        contaViewModel = contaViewModel,
        onLogout = {})

}
