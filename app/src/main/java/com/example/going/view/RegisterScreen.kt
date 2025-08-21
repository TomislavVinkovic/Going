package com.example.going.view

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.going.viewmodel.AuthViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

// TODO: Add validation to the button click
@Composable
fun RegisterScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
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

    var showErrorDialog by remember {mutableStateOf(false)}

    val registerState by authViewModel.registerState.collectAsState()

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
                "Registracija",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value=email,
                onValueChange = {email=it},
                label = {Text("Email adresa")},
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                isError = !isEmailvalid,
                supportingText = {
                    if(!isEmailvalid) {
                        Text(
                            "E-mail adresa nije valjana"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value=password,
                onValueChange = {password=it},
                label={Text("Lozinka")},
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                isError = !isPasswordvalid,
                supportingText = {
                    if(!isPasswordvalid) {
                        Text(
                            "Lozinka mora imati barem 8 znakova, 1 veliko slovo, 1 broj, i jedan spec. znak"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value=repeatPassword,
                onValueChange = {repeatPassword=it},
                label={Text("Ponovljena lozinka")},
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                isError = !isRepeatPasswordvalid,
                supportingText = {
                    if(!isPasswordvalid) {
                        Text(
                            "Lozinke se ne podudaraju"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value=username,
                onValueChange = {username=it},
                label={Text("Korisničko ime")},
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                isError = !isUsernamevalid,
                supportingText = {
                    if(!isUsernamevalid) {
                        Text(
                            "Korisničko ime mora imati barem 6 znakova"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value=firstname,
                onValueChange = {firstname=it},
                label={Text("Ime")},
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                isError = !isFirstnamevalid,
                supportingText = {
                    if(!isUsernamevalid) {
                        Text(
                            "Ime mora imati barem 2 slova, od kojih prvo mora biti veliko"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value=lastname,
                onValueChange = {lastname=it},
                label={Text("Prezime")},
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                isError = !isLastnamevalid,
                supportingText = {
                    if(!isUsernamevalid) {
                        Text(
                            "Prezime mora imati barem 2 slova, od kojih prvo mora biti veliko"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    authViewModel.registerUser(
                        email,
                        password,
                        firstname,
                        lastname,
                        username
                    )
                },
                enabled = !registerState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registriraj se")
            }

            Spacer(modifier = Modifier.height(16.dp))
            if(registerState.isLoading) {
                CircularProgressIndicator()
            }
        }
    }
}