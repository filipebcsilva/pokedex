package com.example.pokedex.Data.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PokemonEntity(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("types") val types: List<TypeSlot>,
    @SerializedName("stats") val stats: List<StatSlot>,
    @SerializedName("sprites") val sprites: Sprites

) : Serializable {
    val typeNames: List<String> get() = types.map { it.type.name }
    val statMap: Map<String, Int> get() = stats.associate { it.stat.name to it.baseStat }
    val imageUrl: String? get() = sprites.frontDefault
    val femaleImageUrl: String? get() = sprites.frontFemale ?: sprites.frontDefault
}

data class TypeSlot(val type: TypeInfo) : Serializable
data class TypeInfo(val name: String) : Serializable

data class StatSlot(
    @SerializedName("base_stat") val baseStat: Int,
    val stat: StatInfo
) : Serializable

data class StatInfo(val name: String) : Serializable

data class Sprites(
    @SerializedName("front_default") val frontDefault: String?,
    @SerializedName("front_female") val frontFemale: String?
) : Serializable
