package com.example.going.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.going.model.ProfileUserData
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class EditProfileUserData(
    val firstname: String?,
    val lastname: String?,
    val isPubliclyInterested: Boolean
)

data class UserDataState (
    val isLoading: Boolean = false,
    val isError: String? = null,
    val isSuccess: String? = null
)

class ProfileViewModel: ViewModel() {
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore
    private var userListener: ListenerRegistration? = null

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _updateUserDataState = MutableStateFlow<UserDataState>(UserDataState())
    val updateUserDataState: StateFlow<UserDataState> = _updateUserDataState;

    private val _userData = MutableStateFlow<ProfileUserData?>(null)
    val userData: StateFlow<ProfileUserData?> = _userData

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                attachUserListener(user.uid)
            } else {
                // Korisnik se odjavio, počisti sve
                detachUserListener()
                _userData.value = null
                _isLoading.value = false
            }
        }
    }

    private fun attachUserListener(uid: String) {
        detachUserListener()
        _isLoading.value = true
        userListener = firestore.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _isLoading.value = false
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    _userData.value = ProfileUserData(
                        uid = uid,
                        firstname = snapshot.getString("firstname"),
                        lastname = snapshot.getString("lastname"),
                        username = snapshot.getString("username"),
                        avatarUrl = snapshot.getString("avatarUrl")
                            ?: auth.currentUser?.photoUrl?.toString(),
                        isPubliclyInterested = snapshot.getBoolean("isPubliclyInterested") ?: true
                    )
                }
                _isLoading.value = false
            }
    }
    private fun detachUserListener() {
        userListener?.remove()
    }

    suspend fun updateProfileInformation(data: EditProfileUserData): Boolean {
        val firebaseUser = auth.currentUser ?: return false // Exit if no user

        _updateUserDataState.value = UserDataState(isLoading = true)

        val userUpdateData = mapOf(
            "firstname" to data.firstname,
            "lastname" to data.lastname,
            "isPubliclyInterested" to data.isPubliclyInterested,
        )

        return try {
            // Use .await() to wait for the operation to complete
            firestore.collection("users")
                .document(firebaseUser.uid)
                .update(userUpdateData)
                .await() // This pauses the function until the update is done

            // If await() succeeds, update the state and return true
            _updateUserDataState.value = UserDataState(isSuccess = "Profile updated successfully")
            true
        } catch (e: Exception) {
            // If await() fails, update the state with the error and return false
            _updateUserDataState.value = UserDataState(isError = "An error occurred during data update")
            false
        }
    }

    override fun onCleared() {
        super.onCleared()
        detachUserListener()
    }
}