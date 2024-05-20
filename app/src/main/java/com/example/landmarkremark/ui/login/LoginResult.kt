package com.example.landmarkremark.ui.login

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
    val success: String? = null,
    val error: Int? = null
)