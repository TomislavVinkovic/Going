package com.example.going.view.ProfileScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.going.viewmodel.ProfileViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.example.going.R
import com.example.going.viewmodel.EditProfileUserData
import kotlinx.coroutines.launch

@Composable
fun EditProfileInformationScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current

    val userData by profileViewModel.userData.collectAsState()
    val updateUserDataState by profileViewModel.updateUserDataState.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState()
    val scope = rememberCoroutineScope()

    var firstname by remember {mutableStateOf(userData?.firstname ?: "")}
    var lastname by remember {mutableStateOf(userData?.lastname ?: "")}
    var isPubliclyInterested by remember {mutableStateOf(userData?.isPubliclyInterested ?: true)}

    var isFirstnamevalid by remember {mutableStateOf(true)}
    var isLastnamevalid by remember {mutableStateOf(true)}

    Box(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp)

    ) {
        if(isLoading && userData == null) {
            CircularProgressIndicator()
        }
        else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.profile_screen_edit_profile_info),
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value=firstname,
                    onValueChange = {firstname=it},
                    label={Text("Ime")},
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth(),
                    isError = !isFirstnamevalid,
                    supportingText = {
                        if(!isFirstnamevalid) {
                            Text(
                                stringResource(R.string.firstname_lastname_validation_error)
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value=lastname,
                    onValueChange = {lastname=it},
                    label={Text("Prezime")},
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth(),
                    isError = !isLastnamevalid,
                    supportingText = {
                        if(!isLastnamevalid) {
                            Text(
                                stringResource(R.string.firstname_lastname_validation_error)
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.profile_screen_profile_public),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = isPubliclyInterested,
                        onCheckedChange = { newCheckedState ->
                            isPubliclyInterested = newCheckedState
                        }
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        val successMessage = context.getString(R.string.data_update_success)
                        // Launch a coroutine to handle the entire update-then-navigate flow
                        scope.launch {
                            val success = profileViewModel.updateProfileInformation(
                                EditProfileUserData(
                                    firstname,
                                    lastname,
                                    isPubliclyInterested
                                )
                            )

                            if (success) {
                                launch {
                                    snackbarHostState.showSnackbar(successMessage)
                                }
                                navController.popBackStack()
                            }
                        }
                    },
                    enabled = !updateUserDataState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Spremi")
                }

                Spacer(modifier = Modifier.height(16.dp))
                if(updateUserDataState.isLoading) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
