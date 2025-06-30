package com.example.mony.feature.conta.menu

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.mony.R
import com.example.mony.feature.conta.classe.UserProfile
import com.example.mony.feature.conta.viewmodel.ContaViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

// Constante para a solicitação de login do Google
private const val RC_SIGN_IN = 9001

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(
    navController: NavController,
    viewModel: ContaViewModel,
    googleSignInClient: GoogleSignInClient // Cliente de autenticação do Google
) {
    val isInPreview = LocalInspectionMode.current
    val sampleProfile = UserProfile(
        name = "João Silva",
        email = "joao@example.com",
        photoUrl = null,
        providerId = "password",
    )

    // Perfil de exemplo ou o perfil carregado do ViewModel
    val userProfile = if (isInPreview) {
        sampleProfile
    } else {
        viewModel.userProfile.collectAsState().value
            ?: return // ainda está carregando, mostra loading
    }

    val isGoogleAccount = userProfile.providerId == "google.com"
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Informação da Conta", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("mais") }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBackIosNew,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(androidx.compose.material3.MaterialTheme.colorScheme.onPrimary)
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
        ) {
            // 1) Loading

            // 2) Conteúdo quando profile estiver disponível
            val profile = userProfile

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Imagem de perfil
                val painter = rememberAsyncImagePainter(
                    model = profile.photoUrl ?: R.drawable.user
                )
                Image(
                    painter = painter,
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(16.dp))

                Text(
                    text = if (isGoogleAccount) "Conta Google" else "Editar Perfil",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = profile.name,
                    onValueChange = {},
                    label = { Text("Nome") },
                    readOnly = isGoogleAccount,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = profile.email,
                    onValueChange = {},
                    label = { Text("E-mail") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = "", // campo de telefone
                    onValueChange = {},
                    label = { Text("Telefone") },
                    readOnly = isGoogleAccount,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = "", // campo de data de nascimento
                    onValueChange = {},
                    label = { Text("Data de Nascimento") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(32.dp))

                if (isGoogleAccount) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(onClick = {
                            // Deslogar o usuário do Google
                            FirebaseAuth.getInstance().signOut()

                            // Agora, inicie o processo de login do Google novamente
                            val signInIntent = googleSignInClient.signInIntent
                            (context as? Activity)?.startActivityForResult(signInIntent, RC_SIGN_IN)
                        }) {
                            Text("Trocar Conta")
                        }

                        TextButton(
                            colors = ButtonDefaults.buttonColors(Color.Red),
                            onClick = {
                                FirebaseAuth.getInstance().signOut()
                                navController.navigate("login") {
                                    popUpTo("info") { inclusive = true }
                                }
                            }
                        ) {
                            Text("Sair")
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            // TODO: Validar e salvar no Firestore
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Salvar Alterações")
                    }
                }
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun InfoScreenPreview() {
    val viewModel = ContaViewModel()

    InfoScreen(
        navController = rememberNavController(),
        viewModel = ContaViewModel(),
        googleSignInClient = GoogleSignIn.getClient(LocalContext.current, GoogleSignInOptions.DEFAULT_SIGN_IN)
    )
}


