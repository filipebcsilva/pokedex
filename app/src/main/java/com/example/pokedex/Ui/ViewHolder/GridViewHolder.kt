package com.example.pokedex.Ui.ViewHolder

import androidx.recyclerview.widget.RecyclerView
import com.example.pokedex.Data.api.PokemonItem
import com.example.pokedex.Ui.SpriteLoader
import com.example.pokedex.databinding.GridItemBinding

class GridViewHolder(
    private val binding: GridItemBinding,
    private val onClick: (PokemonItem) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: PokemonItem) {
        binding.namePokemon.text = item.name.replaceFirstChar { it.uppercase() }
        SpriteLoader.load(binding.imagePokemon, item.imageUrl)
        binding.root.setOnClickListener { onClick(item) }
    }
}
