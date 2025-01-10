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
import com.example.mony.R
import com.example.mony.feature.home.HomeActivity
import com.example.mony.feature.utils.navegation.MyApp
import com.example.mony.ui.theme.Roxo

class LoginActivity : ComponentActivity() {
    private var mAuth: FirebaseAuth? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()


        // Configuração do Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Use a chave correta do strings.xml
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)


        // Verificar se o usuário já está logado
        val currentUser = mAuth!!.currentUser
        if (currentUser != null) {
            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Usar o Compose para exibir a tela de login
        setContent {
            LoginScreen(
                onGoogleSignInClick = { googleSignIn() }
            )
        }
    }

    private fun googleSignIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken)
        } catch (e: ApiException) {
            Log.w("LoginActivity", "signInResult:failed code=" + e.statusCode, e)
            Toast.makeText(this, "Falha no login: " + e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this) { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    val user = mAuth!!.currentUser
                    Log.d("LoginActivity", "Usuário conectado: " + user?.email)
                    Toast.makeText(this, "Login bem-sucedido: ${user?.email}", Toast.LENGTH_SHORT).show()

                    // Redireciona para o HomeActivity
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish() // Finaliza o LoginActivity
                } else {
                    Log.w("LoginActivity", "Erro na autenticação com Firebase", task.exception)
                    Toast.makeText(this, "Autenticação falhou.", Toast.LENGTH_SHORT).show()
                }
            }
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

