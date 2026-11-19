package com.example.pokedex.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pokedex.Data.api.PokemonEntity
import com.example.pokedex.Data.remote.FavoritesRepository
import com.google.firebase.firestore.ListenerRegistration

class DetailViewModel : ViewModel() {

    private val repository = FavoritesRepository()
    private var registro: ListenerRegistration? = null
    private var pokemon: PokemonEntity? = null

    private val _favorito = MutableLiveData(false)
    val favorito: LiveData<Boolean> = _favorito

    private val _erro = MutableLiveData(false)
    val erro: LiveData<Boolean> = _erro

    fun iniciar(p: PokemonEntity) {
        if (pokemon != null) return
        pokemon = p
        registro = repository.observeIsFavorite(p.id) { _favorito.value = it }
    }

    fun alternar(): Boolean {
        val p = pokemon ?: return false
        val agoraFavorito = _favorito.value != true
        definir(p, agoraFavorito)
        return agoraFavorito
    }

    fun desfazer(eraFavorito: Boolean) {
        pokemon?.let { definir(it, eraFavorito) }
    }

    private fun definir(p: PokemonEntity, favorito: Boolean) {
        _favorito.value = favorito
        val onError: (Exception) -> Unit = { _erro.value = true }
        if (favorito) repository.add(p, onError) else repository.remove(p.id, onError)
    }

    fun erroExibido() {
        _erro.value = false
    }

    override fun onCleared() {
        registro?.remove()
    }
}
