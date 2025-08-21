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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.going.viewmodel.ProfileUserData
import com.example.going.R
import com.example.going.util.ProfileScreen

@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel
) {

    // Viewmodel state
    val isLoading by profileViewModel.isLoading.collectAsState()
    val userData by profileViewModel.userData.collectAsState()
    val updateUserDataState by profileViewModel.updateUserDataState.collectAsState()

    // UI state
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var pendingPublicInterestState by remember {mutableStateOf(false)}

    Box(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp)

    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            ProfileCard(isLoading, userData)

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {}
            ) {
                Text(stringResource(R.string.profile_screen_edit_profile_picture))
            }
            TextButton(
                onClick = {
                    navController.navigate(ProfileScreen.EditProfileInformation.route)
                }
            ) {
                Text(stringResource(R.string.profile_screen_edit_profile_info))
            }
        }
    }
}

@Composable
fun ProfileCard(
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
        }
    }
}