package com.example.going.view.Auth

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.going.viewmodel.AuthViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.going.R
import com.example.going.util.Screen
import com.example.going.view.common.MessageDialog
import kotlinx.coroutines.launch

@Composable
fun PasswordFieldTrailingButton(
    isVisible: Boolean,
    onClick: () -> Unit
) {
    val image = if(isVisible) Icons.Filled.Visibility
    else Icons.Filled.VisibilityOff
    val descriptionText =
        if(isVisible)
            stringResource(R.string.register_screen_hide_password)
        else
            stringResource(R.string.register_screen_show_password)

    IconButton(onClick) {
        Icon(imageVector = image, descriptionText)
    }
}

@Composable
fun RegisterScreen(
    navController: NavController,
    mainNavController: NavHostController,
    authViewModel: AuthViewModel = viewModel(),
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val registerState by authViewModel.registerState.collectAsState()

    var email by remember {mutableStateOf("")}
    var username by remember {mutableStateOf("")}
    var password by remember {mutableStateOf("")}
    var repeatPassword by remember {mutableStateOf("")}
    var firstname by remember {mutableStateOf("")}
    var lastname by remember {mutableStateOf("")}

    var isEmailvalid by remember {mutableStateOf(true)}
    var isUsernamevalid by remember {mutableStateOf(true)}
    var isPasswordvalid by remember {mutableStateOf(true)}
    var isRepeatPasswordvalid by remember {mutableStateOf(true)}
    var isFirstnamevalid by remember {mutableStateOf(true)}
    var isLastnamevalid by remember {mutableStateOf(true)}

    // UI control
    var passwordVisible by remember { mutableStateOf(false) }
    var repeatPasswordVisible by remember { mutableStateOf(false) }

    var showErrorDialog by remember {mutableStateOf(false)}

    fun validateFields(): Boolean {
        isEmailvalid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        isUsernamevalid = username.length >= 6

        // Password validation
        val passwordRegex = Regex("^(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#\$%^&*]).{8,}\$")
        isPasswordvalid = passwordRegex.matches(password)
                && repeatPassword == password
        isRepeatPasswordvalid = password == repeatPassword

        val nameRegex = Regex("^[A-Z][a-z0-9]+\$")
        isFirstnamevalid = nameRegex.matches(firstname)
        isLastnamevalid = nameRegex.matches(firstname)

        return isEmailvalid
                && isUsernamevalid
                && isPasswordvalid
                && isRepeatPasswordvalid
                && isFirstnamevalid
                && isLastnamevalid
    }

    LaunchedEffect(registerState) {
        if(registerState.isError != null) {
            showErrorDialog = true
        }
        if(registerState.isSuccess != null) {
            mainNavController.navigate(Screen.MainApp.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    if(showErrorDialog) {
        MessageDialog(
            onClose = {
                showErrorDialog = false
                authViewModel.clearRegisterState()
            },
            message = registerState.isError!!
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Text(
            stringResource(R.string.register_screen_title),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value=email,
            onValueChange = {email=it},
            label = {Text(stringResource(R.string.register_screen_email))},
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            isError = !isEmailvalid,
            supportingText = {
                if(!isEmailvalid) {
                    Text(stringResource(R.string.email_validation_error))
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value=password,
            onValueChange = {password=it},
            label={Text(stringResource(R.string.register_screen_password))},
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation =
                if (passwordVisible)
                    VisualTransformation.None
                else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = !isPasswordvalid,
            supportingText = {
                if(!isPasswordvalid) {
                    Text(stringResource(R.string.password_validation_error))
                }
            },
            trailingIcon = {
                PasswordFieldTrailingButton(
                    isVisible = passwordVisible,
                    onClick = {passwordVisible = !passwordVisible}
                )
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value=repeatPassword,
            onValueChange = {repeatPassword=it},
            label={Text(stringResource(R.string.register_screen_repeat_password))},
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation =
                if (repeatPasswordVisible)
                    VisualTransformation.None
                else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = !isRepeatPasswordvalid,
            supportingText = {
                if(!isPasswordvalid) {
                    Text(stringResource(R.string.repeat_password_validation_error))
                }
            },
            trailingIcon = {
                PasswordFieldTrailingButton(
                    isVisible = repeatPasswordVisible,
                    onClick = {repeatPasswordVisible = !repeatPasswordVisible}
                )
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value=username,
            onValueChange = {username=it},
            label={Text(stringResource(R.string.register_screen_username))},
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth(),
            isError = !isUsernamevalid,
            supportingText = {
                if(!isUsernamevalid) {
                    Text(stringResource(R.string.username_validation_error))
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value=firstname,
            onValueChange = {firstname=it},
            label={Text(stringResource(R.string.register_screen_firstname))},
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth(),
            isError = !isFirstnamevalid,
            supportingText = {
                if(!isFirstnamevalid) {
                    Text(stringResource(R.string.firstname_lastname_validation_error))
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value=lastname,
            onValueChange = {lastname=it},
            label={Text(stringResource(R.string.register_screen_lastname))},
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth(),
            isError = !isLastnamevalid,
            supportingText = {
                if(!isLastnamevalid) {
                    Text(stringResource(R.string.firstname_lastname_validation_error))
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if(validateFields()) {
                    scope.launch {
                        authViewModel.registerUser(
                            email,
                            password,
                            firstname,
                            lastname,
                            username
                        )
                    }
                }
            },
            enabled = !registerState.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if(registerState.isLoading) {
                CircularProgressIndicator()
            }
            else {
                Text(stringResource(R.string.register_screen_register_button_text))
            }
        }
    }
}
