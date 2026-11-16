package com.example.pokedex.Data.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonService {

    @GET("pokemon/{name}")
    fun getPokemon(@Path("name") name: String): Call<PokemonEntity>

    @GET("pokemon")
    fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Call<PokemonListResponse>
}