package com.example.going.model

data class ProfileUserData(
    val uid: String,
    val firstname: String?,
    val lastname: String?,
    val username: String?,
    val avatarUrl: String?,
    val isPubliclyInterested: Boolean?
)