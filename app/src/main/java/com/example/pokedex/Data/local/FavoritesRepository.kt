package com.example.pokedex.Data.local

import android.content.Context
import com.example.pokedex.Data.api.PokemonEntity

class FavoritesRepository(context: Context) {

    private val dao = AppDatabase.getDatabase(context).FavoriteDAO()

    fun getAll(): List<FavoriteModel> = dao.getAll()

    fun isFavorite(id: Int): Boolean = dao.getById(id) != null

    fun toggle(pokemon: PokemonEntity): Boolean {
        val existing = dao.getById(pokemon.id)
        return if (existing != null) {
            dao.delete(existing)
            false
        } else {
            dao.insert(FavoriteModel().apply {
                id = pokemon.id
                name = pokemon.name
                imageUrl = pokemon.imageUrl
            })
            true
        }
    }

    fun remove(f: FavoriteModel) = dao.delete(f)
}
