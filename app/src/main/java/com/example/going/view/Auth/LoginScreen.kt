package com.example.going.view.Auth

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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.going.util.Screen
import com.example.going.viewmodel.AuthViewModel
import com.example.going.R
import com.example.going.util.AuthScreen
import com.example.going.view.common.ConfirmDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavHostController,
    mainNavController: NavHostController,
    authViewModel: AuthViewModel = viewModel(),
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
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
        if(loginState.isError != null) {
            showErrorDialog = true
        }
    }

    // Error dialog
    if(showErrorDialog) {
        val loginFailureTitle = context.getString(R.string.login_failure)
        val loginFailureTextDefault = context.getString(R.string.data_update_success)
        ConfirmDialog(
            {
                showErrorDialog = false
            },
            {
                showErrorDialog = false
            },
            title=loginFailureTitle,
            text=loginState.isError?: loginFailureTextDefault

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
            Text(
                text = stringResource(R.string.login_screen_title),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value=email,
                onValueChange = {email = it},
                label = {Text(stringResource(R.string.login_screen_email))},
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = !isEmailValid,
                supportingText = {
                    if(!isEmailValid) {
                        Text(
                            stringResource(R.string.email_validation_error)
                        )
                    }
                },
                modifier=Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value=password,
                onValueChange = {password=it},
                label={Text(stringResource(R.string.login_screen_password))},
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                isError = !isPasswordValid,
                supportingText = {
                    if(!isPasswordValid) {
                        Text(
                            stringResource(R.string.password_validation_error)
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
                Text(text = stringResource(R.string.login_screen_login))
            }

            Spacer(modifier = Modifier.height(8.dp) )

            if(loginState.isLoading) {
                CircularProgressIndicator()
            }

        }
    }
}