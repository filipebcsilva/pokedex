package com.example.pokedex.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pokedex.Data.api.PokemonEntity
import com.example.pokedex.Data.api.PokemonItem
import com.example.pokedex.Data.api.PokemonListResponse
import com.example.pokedex.Data.api.PokemonService
import com.example.pokedex.Data.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GridViewModel : ViewModel() {

    private val service = RetrofitClient.createService(PokemonService::class.java)

    private val _pokemons = MutableLiveData<List<PokemonItem>>(emptyList())
    val pokemons: LiveData<List<PokemonItem>> = _pokemons

    private val _carregando = MutableLiveData(false)
    val carregando: LiveData<Boolean> = _carregando

    private val _erro = MutableLiveData<String?>()
    val erro: LiveData<String?> = _erro

    private val _selecionado = MutableLiveData<PokemonEntity?>()
    val selecionado: LiveData<PokemonEntity?> = _selecionado

    private var offset = 0
    private var temMais = true

    fun carregarProximaPagina() {
        if (_carregando.value == true || !temMais) return
        _carregando.value = true

        service.getPokemonList(PAGE_SIZE, offset).enqueue(object : Callback<PokemonListResponse> {
            override fun onResponse(
                call: Call<PokemonListResponse>,
                response: Response<PokemonListResponse>
            ) {
                _carregando.value = false
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    offset += body.results.size
                    temMais = body.next != null
                    _pokemons.value = _pokemons.value.orEmpty() + body.results
                    _erro.value = null
                } else {
                    _erro.value = "Erro ao carregar a lista"
                }
            }

            override fun onFailure(call: Call<PokemonListResponse>, t: Throwable) {
                _carregando.value = false
                _erro.value = "Sem conexão"
            }
        })
    }

    fun selecionar(item: PokemonItem) {
        service.getPokemon(item.name).enqueue(object : Callback<PokemonEntity> {
            override fun onResponse(call: Call<PokemonEntity>, response: Response<PokemonEntity>) {
                if (response.isSuccessful) _selecionado.value = response.body()
                else _erro.value = "Pokémon não encontrado"
            }

            override fun onFailure(call: Call<PokemonEntity>, t: Throwable) {
                _erro.value = "Sem conexão"
            }
        })
    }

    fun limparSelecao() {
        _selecionado.value = null
    }

    fun limparErro() {
        _erro.value = null
    }

    companion object {
        const val PAGE_SIZE = 30
    }
}
