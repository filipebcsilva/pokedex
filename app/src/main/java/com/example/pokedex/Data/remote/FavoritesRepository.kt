package com.example.pokedex.Data.remote

import com.example.pokedex.Data.api.PokemonEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class FavoritesRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private fun collection(): CollectionReference? {
        val uid = auth.currentUser?.uid ?: return null
        return db.collection(USERS).document(uid).collection(FAVORITES)
    }

    fun observeAll(
        onChange: (List<FavoriteModel>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration? =
        collection()?.orderBy("id", Query.Direction.ASCENDING)
            ?.addSnapshotListener { snapshot, error ->
                if (error != null) onError(error)
                else onChange(snapshot?.toObjects(FavoriteModel::class.java).orEmpty())
            }

    fun observeIsFavorite(id: Int, onChange: (Boolean) -> Unit): ListenerRegistration? =
        collection()?.document(id.toString())
            ?.addSnapshotListener { snapshot, _ -> onChange(snapshot?.exists() == true) }

    fun add(pokemon: PokemonEntity, onError: (Exception) -> Unit = {}) {
        val favorite = FavoriteModel(pokemon.id, pokemon.name, pokemon.imageUrl)
        collection()?.document(pokemon.id.toString())?.set(favorite)
            ?.addOnFailureListener(onError)
    }

    fun remove(id: Int, onError: (Exception) -> Unit = {}) {
        collection()?.document(id.toString())?.delete()?.addOnFailureListener(onError)
    }

    companion object {
        const val USERS = "users"
        const val FAVORITES = "favorites"
    }
}
