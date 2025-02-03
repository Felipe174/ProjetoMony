package com.example.mony.feature.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import androidx.compose.material3.Text // Para usar o Text do Material Design
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.mony.R
import com.example.mony.feature.home.HomeActivity
import com.example.mony.feature.login.viewmodel.LoginState
import com.example.mony.feature.login.viewmodel.LoginViewModel
import com.example.mony.feature.utils.navegation.MyApp
import com.example.mony.ui.theme.Roxo
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private val viewModel: LoginViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(FirebaseAuth.getInstance()) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuração do Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Observando mudanças no estado de login
        lifecycleScope.launch {
            viewModel.loginState.collectLatest { state ->
                when (state) {
                    is LoginState.Success -> navigateToHome()
                    is LoginState.Error -> showError(state.message)
                    LoginState.Loading -> showLoading()
                    else -> Unit
                }
            }
        }

        setContent {
            LoginScreen(
                onGoogleSignInClick = { startGoogleSignIn() }
            )
        }
    }

    private fun startGoogleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                account?.let { viewModel.handleGoogleSignInResult(it) }
            } catch (e: ApiException) {
                viewModel.resetState()
                Log.e("LoginActivity", "Google sign in failed", e)
            }
        }
    }

    private fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showLoading() {
        // Implementar um indicador de carregamento, se necessário
    }

    companion object {
        private const val RC_SIGN_IN = 100
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onGoogleSignInClick: () -> Unit) {
    Scaffold { paddingValues ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Referências para os componentes
            val (image, textGroup, buttonGroup) = createRefs()

            // Imagem principal no topo
            Image(
                painter = painterResource(id = R.drawable.login_img),
                contentDescription = "Imagem 1",
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(image) {
                        top.linkTo(parent.top, margin = 80.dp) // Espaçamento do topo
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            // Textos abaixo da imagem
            Column(
                modifier = Modifier
                    .constrainAs(textGroup) {
                        top.linkTo(image.bottom, margin = 16.dp) // Abaixo da imagem
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp) // Espaçamento entre textos
            ) {
                Text(
                    text = "Olá",
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 55.sp),
                   // fontFamily =
                )
                Text(
                    text = "Bem Vindo ao Mony",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                    //fontFamily = Robotomedium,
                    color = Color.Gray
                )
                Text(
                    text = "onde você irá gerenciar o seu dinheiro",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                    //fontFamily = Robotomedium,
                    color = Color.Gray
                )
            }

            // Botões e imagem menor
            Column(
                modifier = Modifier
                    .constrainAs(buttonGroup) {
                        top.linkTo(textGroup.bottom, margin = 115.dp) // Abaixo do grupo de textos
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FilledTonalButton(
                    onClick = { /* Ação do botão 1 */ },
                    modifier = Modifier.width(200.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Roxo // Cor de fundo do botão
                    )
                ) {
                    Text("Login")
                }

                OutlinedButton(onClick = { /* Ação do botão 2 */ },
                    modifier = Modifier.width(200.dp)) {
                    Text("Registra-se", ) }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ou faça login com",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                   // fontFamily = Robotomedium,
                    color = Color.Gray,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Image(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "imgGoogle",
                    modifier = Modifier.size(25.dp)
                        .clickable { onGoogleSignInClick() }
                )


            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    // Aplica o tema de Material3 à visualização
    MaterialTheme {
        LoginScreen(onGoogleSignInClick = {})
    }
}

