package com.example.going.view.Auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.example.going.util.Screen
import com.example.going.viewmodel.AuthViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.example.going.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.going.util.AuthScreen
import com.example.going.view.common.ConfirmDialog


@Composable
fun GreetingScreen(
    navController: NavHostController,
    mainNavController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val googleSignInState by authViewModel.googleSignInState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val credentialManager = remember {
        CredentialManager.create(context)
    }

    fun signInWithGoogle() {
        coroutineScope.launch {
            try {
                // 2. Kreiranje zahtjeva za Google ID tokenom
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.default_web_client_id))
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                // 3. Pokretanje zahtjeva i dohvaćanje rezultata
                val result = credentialManager.getCredential(
                    context = context,
                    request = request
                )

                // 4. Obrada rezultata
                val credential = result.credential
                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken
                    // 5. Prosljeđivanje tokena ViewModelu
                    authViewModel.loginUserWithGoogle(idToken)
                } else {
                    // Obrada slučaja ako credential nije očekivanog tipa
                }

            } catch (e: Exception) {
                // Obrada greške (npr. korisnik je otkazao prijavu)
            }
        }
    }

    var showErrorDialog by remember { mutableStateOf(false) }

    // Error dialog
    if(showErrorDialog) {

        val titleText = context.getString(R.string.login_failure)
        val descText = context.getString(R.string.unknown_failure)

        ConfirmDialog(
            onConfirm = {
                showErrorDialog = false
                authViewModel.clearGoogleLoginState()
            },
            onDismiss = {
                showErrorDialog = false
            },
            title = titleText,
            text = descText
        )
    }

    LaunchedEffect(googleSignInState) {
        if(googleSignInState.isError != null) {
            showErrorDialog = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                stringResource( R.string.app_name),
                style = TextStyle(
                    fontSize = 48.sp,
                    color = MaterialTheme.colorScheme.inverseSurface
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    navController.navigate(AuthScreen.Login.route)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.greeting_screen_email_login))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    signInWithGoogle()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.greeting_screen_google_login))
            }

            Spacer(modifier = Modifier.height(32.dp))


            Text(
                stringResource(R.string.greeting_screen_register_invitation_1),
                style = TextStyle(
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton (
                onClick = {
                    navController.navigate(AuthScreen.Register.route)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.greeting_screen_register_invitation_2),)
            }
        }
    }
}