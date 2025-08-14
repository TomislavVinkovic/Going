package com.example.going.viewmodel

import android.util.Log.e
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AuthState (
    val isLoading: Boolean = false,
    val isSuccess: String? = null,
    val isError: String? = null
)

class AuthViewModel: ViewModel() {
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn

    private val _registerState = MutableStateFlow<AuthState>(AuthState())
    val registerState: StateFlow<AuthState> = _registerState

    private val _loginState = MutableStateFlow<AuthState>(AuthState())
    val loginState: StateFlow<AuthState> = _loginState

    private val _googleSignInState = MutableStateFlow(AuthState())
    val googleSignInState: StateFlow<AuthState> = _googleSignInState

    private val _logoutState = MutableStateFlow<AuthState>(AuthState())
    val logoutState: StateFlow<AuthState> = _logoutState

    init {
        checkCurrentUser()
    }

    // Auth methods
    private fun checkCurrentUser() {
        viewModelScope.launch {
            if(auth.currentUser != null) {
                _isUserLoggedIn.value = true
            }
            _isLoading.value = false
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = AuthState(isLoading = true)
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                // TODO: Add localization
                _loginState.value = AuthState(isSuccess = "Prijava uspješna")
            } catch(e: Exception) {
                _loginState.value = AuthState(isError = e.message ?: "Nepoznata greška.")
            }
        }
    }

    fun loginUserWithGoogle(idToken: String) {
        viewModelScope.launch {
            _googleSignInState.value = AuthState(isLoading = true)
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = auth.signInWithCredential(credential).await()
                val firebaseUser = result.user!!

                // Check if the user is new. If the user is new, save their info to firestore
                val isNewUser = result.additionalUserInfo?.isNewUser ?: false
                if(isNewUser) {
                    saveGoogleUserToFirestore(firebaseUser)
                }
                _googleSignInState.value = AuthState(isSuccess = "Prijava uspješna")
            } catch(e: Exception) {
                _googleSignInState.value = AuthState(isError = e.message ?: "Nepoznata greška")
            }
        }
    }

    fun registerUser(
        email: String,
        password: String,
        firstname: String,
        lastname: String,
        username: String,
    ) {
        viewModelScope.launch {
            _registerState.value = AuthState(isLoading = true)
            try {
                val usernameQuery = firestore.collection("users")
                    .whereEqualTo("username", username)
                    .get()
                    .await()
                if(!usernameQuery.isEmpty) {
                    _registerState.value = AuthState(isError = "Korisničko ime je već zauzeto")
                    return@launch
                }
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = result.user!!

                saveUserToFirestore(firebaseUser, firstname, lastname, username)
                _registerState.value = AuthState(isSuccess = "Registracija korisnika uspješna!")
            } catch(e: Exception) {
                _registerState.value = AuthState(isError = e.message ?: "Nepoznata greška.")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _logoutState.value = AuthState(isLoading = true)
            try {
                auth.signOut()
                _logoutState.value = AuthState(isSuccess = "Odjava uspješna")
            } catch (e: Exception) {
                _logoutState.value = AuthState(isError = e.message)
            }
        }
    }

    private suspend fun saveUserToFirestore(
        firebaseUser: FirebaseUser,
        firstname: String,
        lastname: String,
        username: String
    ) {
        val user = hashMapOf(
            "firstname" to firstname,
            "lastname" to lastname,
            "username" to username,
            "email" to firebaseUser.email,
            "isPubliclyInterested" to true,
            "createdAt" to System.currentTimeMillis()
        )

        firestore
            .collection("users")
            .document(firebaseUser.uid)
            .set(user)
            .await()
    }

    private suspend fun saveGoogleUserToFirestore(firebaseUser: FirebaseUser) {
        val user = hashMapOf(
            "firstname" to (firebaseUser.displayName?.split(" ")?.firstOrNull() ?: ""),
            "lastname" to (firebaseUser.displayName?.split(" ")?.getOrNull(1) ?: ""),
            "username" to (firebaseUser.email?.split("@")?.first() ?: ""),
            "email" to firebaseUser.email,
            "isPubliclyInterested" to true,
            "createdAt" to System.currentTimeMillis()
        )

        firestore
            .collection("users")
            .document(firebaseUser.uid)
            .set(user)
            .await()
    }

    fun clearGoogleLoginState() {
        _googleSignInState.value = AuthState()
    }
}

