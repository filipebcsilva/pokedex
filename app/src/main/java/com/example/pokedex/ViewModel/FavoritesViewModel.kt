package com.example.pokedex.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pokedex.Data.api.PokemonEntity
import com.example.pokedex.Data.api.PokemonService
import com.example.pokedex.Data.api.RetrofitClient
import com.example.pokedex.Data.remote.AuthRepository
import com.example.pokedex.Data.remote.FavoriteModel
import com.example.pokedex.Data.remote.FavoritesRepository
import com.example.pokedex.R
import com.google.firebase.firestore.ListenerRegistration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoritesViewModel : ViewModel() {

    private val repository = FavoritesRepository()
    private val auth = AuthRepository()
    private val service = RetrofitClient.createService(PokemonService::class.java)

    private val _favoritos = MutableLiveData<List<FavoriteModel>>(emptyList())
    val favoritos: LiveData<List<FavoriteModel>> = _favoritos

    private val _selecionado = MutableLiveData<PokemonEntity?>()
    val selecionado: LiveData<PokemonEntity?> = _selecionado

    private val _mensagem = MutableLiveData<Int?>()
    val mensagem: LiveData<Int?> = _mensagem

    private val registro: ListenerRegistration? = repository.observeAll(
        onChange = { _favoritos.value = it },
        onError = { _mensagem.value = R.string.error_load_favorites }
    )

    fun remover(f: FavoriteModel) {
        repository.remove(f.id) { _mensagem.value = R.string.error_generic }
    }

    fun abrirDetalhe(f: FavoriteModel) {
        service.getPokemon(f.name).enqueue(object : Callback<PokemonEntity> {
            override fun onResponse(call: Call<PokemonEntity>, response: Response<PokemonEntity>) {
                val pokemon = response.body()
                if (response.isSuccessful && pokemon != null) _selecionado.value = pokemon
                else _mensagem.value = R.string.error_pokemon_not_found
            }

            override fun onFailure(call: Call<PokemonEntity>, t: Throwable) {
                _mensagem.value = R.string.error_no_connection
            }
        })
    }

    fun sair() = auth.logout()

    fun limparSelecao() {
        _selecionado.value = null
    }

    fun mensagemExibida() {
        _mensagem.value = null
    }

    override fun onCleared() {
        registro?.remove()
    }
}
