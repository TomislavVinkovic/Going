package com.example.going.view

import android.R.attr.onClick
import android.R.attr.password
import android.R.attr.singleLine
import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.going.util.Screen
import com.example.going.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember {mutableStateOf("")}

    var isEmailValid by remember {mutableStateOf(true)}
    var isPasswordValid by remember {mutableStateOf(true)}

    var showErrorDialog by remember {mutableStateOf(false)}

    val loginState by authViewModel.loginState.collectAsState()

    fun validateFields(): Boolean {
        // Check email validity by using the built in Android pattern
        isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val passwordRegex = Regex("^(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#\$%^&*]).{8,}\$")
        isPasswordValid = passwordRegex.matches(password)

        return isEmailValid && isPasswordValid
    }

    LaunchedEffect(loginState) {
        if(loginState.isSuccess != null) {
            navController.navigate(Screen.MainApp.route) {
                popUpTo(Screen.Greeting.route) {inclusive = true}
            }
        }
        if(loginState.isError != null) {
            showErrorDialog = true
        }
    }

    // Error dialog
    if(showErrorDialog) {
        AlertDialog(
            onDismissRequest = {
                showErrorDialog = false
                // Resetiraj stanje greške u ViewModelu da se dijalog ne prikaže ponovno
                // authViewModel.clearLoginError()
            },
            title = { Text("Greška pri prijavi") },
            text = { Text(loginState.isError ?: "Došlo je do nepoznate greške.") },
            confirmButton = {
                Button(
                    onClick = {
                        showErrorDialog = false
                        // Resetiraj stanje greške u ViewModelu
                        // authViewModel.clearLoginError()
                    }
                ) {
                    Text("U redu")
                }
            }
        )
    }

    // UI components
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
            // TODO: Add translations
            Text(
                text = "Dobrodošli natrag",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value=email,
                onValueChange = {email = it},
                label = {Text("Email adresa")},
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = !isEmailValid,
                supportingText = {
                    if(!isEmailValid) {
                        Text(
                            "E-mail adresa nije valjana"
                        )
                    }
                },
                modifier=Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value=password,
                onValueChange = {password=it},
                label={Text("Lozinka")},
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                isError = !isPasswordValid,
                supportingText = {
                    if(!isPasswordValid) {
                        Text(
                            "Lozinka mora imati barem 8 znakova, 1 veliko slovo, 1 broj, i jedan spec. znak"
                        )
                    }
                },
                modifier=Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if(validateFields()) {
                        authViewModel.loginUser(email, password)
                    }
                },
                enabled = !loginState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Prijavi se")
            }

            Spacer(modifier = Modifier.height(8.dp) )

            if(loginState.isLoading) {
                CircularProgressIndicator()
            }

        }
    }
}