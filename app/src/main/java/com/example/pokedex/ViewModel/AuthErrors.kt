package com.example.pokedex.ViewModel

import com.example.pokedex.R
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

fun Exception.toAuthMessage(): Int = when (this) {
    is FirebaseAuthWeakPasswordException -> R.string.error_weak_password
    is FirebaseAuthUserCollisionException -> R.string.error_email_in_use
    is FirebaseAuthInvalidUserException -> R.string.error_invalid_login
    is FirebaseAuthInvalidCredentialsException -> R.string.error_invalid_login
    is FirebaseNetworkException -> R.string.error_no_connection
    else -> R.string.error_generic
}
