package com.example.going.view.ProfileScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.example.going.viewmodel.ProfileViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.going.viewmodel.ProfileUserData
import com.example.going.R
import com.example.going.util.ProfileScreen
import com.example.going.util.Screen
import com.example.going.view.common.ConfirmDialog
import com.example.going.view.common.MessageDialog
import com.example.going.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Viewmodel state
    val isLoading by profileViewModel.isLoading.collectAsState()
    val userData by profileViewModel.userData.collectAsState()
    val logoutState by authViewModel.logoutState.collectAsState()

    var showLogoutConfirmationDialog by remember { mutableStateOf(false) }
    var showLogoutErrorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(logoutState) {
        if (logoutState.isSuccess != null) {
            navController.navigate(
                Screen.Auth.route
            ) {
                popUpTo(0) { inclusive = true }
            }
            authViewModel.clearLogoutState()
        }
        else if(logoutState.isError != null) {
            showLogoutErrorDialog = true
        }
    }

    fun openLogoutConfirmationDialog() {
        showLogoutConfirmationDialog = true
    }
    fun openLogoutErrorDialog() {
        showLogoutErrorDialog = true
    }

    fun logout() {
        openLogoutConfirmationDialog()
    }

    if(showLogoutConfirmationDialog) {
        val logoutConfirmationTitle = context.getString(R.string.profile_screen_logout)
        val logoutConfirmationText = context.getString(R.string.profile_screen_logout_desc)
        val logoutConfirmationButtonText
            = context.getString(R.string.profile_screen_logout_confirm)

        ConfirmDialog(
            {
                showLogoutConfirmationDialog = false
                authViewModel.logout(context)
            },
            {
                showLogoutConfirmationDialog = false
            },
            logoutConfirmationTitle,
            logoutConfirmationText,
            logoutConfirmationButtonText
        )
    }
    if(showLogoutErrorDialog) {
        val logoutErrorText = logoutState.isError!!
        MessageDialog(
            onClose = {
                showLogoutErrorDialog = false
                authViewModel.clearLogoutState()
            },
            message = logoutErrorText
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp)

    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            ProfileCard(navController, isLoading, userData)

            Button(
                onClick = {
                    logout()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.profile_screen_logout))
            }
        }
    }
}

@Composable
fun ProfileCard(
    navController: NavController,
    isLoading: Boolean,
    userData: ProfileUserData?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        if(isLoading && userData == null) {
            CircularProgressIndicator(modifier = Modifier.padding(10.dp, 51.dp))
        }
        else {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Picture
                AsyncImage(
                    model = userData?.avatarUrl,
                    contentDescription = stringResource(R.string.profile_screen_profile_picture),
                    modifier = Modifier
                        .clickable(onClick = {
                            // TODO: Implement profile picture updates
                        })
                        .size(80.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.ic_launcher_background), // Replace with your placeholder
                    error = painterResource(id = R.drawable.ic_launcher_background) // Replace with your placeholder
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Name and Username
                Column {
                    Text(
                        text = userData?.firstname ?: stringResource(R.string.profile_screen_user),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "@${userData?.username ?: stringResource(R.string.profile_screen_username_placeholder)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
            TextButton(
                onClick = {
                    navController.navigate(ProfileScreen.EditProfileInformation.route)
                },
                modifier = Modifier.padding(16.dp, 8.dp)
            ) {
                Text(stringResource(R.string.profile_screen_edit_profile_info))
            }
        }
    }
}