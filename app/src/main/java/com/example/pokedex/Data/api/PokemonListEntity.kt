package com.example.pokedex.Data.api

data class PokemonListResponse(
    val next: String?,
    val results: List<PokemonItem>
)

data class PokemonItem(val name: String, val url: String) {
    val id: Int get() = url.trimEnd('/').substringAfterLast('/').toInt()
    val imageUrl: String get() =
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
}