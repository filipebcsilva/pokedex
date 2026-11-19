package com.example.pokedex.Data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    val isLoggedIn: Boolean get() = auth.currentUser != null

    fun login(email: String, password: String, onResult: (Exception?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onResult(null) }
            .addOnFailureListener { onResult(it) }
    }

    fun register(
        name: String,
        nick: String,
        email: String,
        password: String,
        onResult: (Exception?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val user = result.user ?: return@addOnSuccessListener onResult(null)
                user.updateProfile(userProfileChangeRequest { displayName = nick })
                val profile = mapOf("name" to name, "nick" to nick, "email" to email)
                db.collection(FavoritesRepository.USERS).document(user.uid).set(profile)
                onResult(null)
            }
            .addOnFailureListener { onResult(it) }
    }

    fun sendPasswordReset(email: String, onResult: (Exception?) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener { onResult(null) }
            .addOnFailureListener { onResult(it) }
    }

    fun logout() = auth.signOut()
}
