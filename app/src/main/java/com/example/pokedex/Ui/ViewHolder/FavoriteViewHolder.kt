package com.example.pokedex.Ui.ViewHolder

import androidx.recyclerview.widget.RecyclerView
import com.example.pokedex.Data.remote.FavoriteModel
import com.example.pokedex.Ui.SpriteLoader
import com.example.pokedex.databinding.FavoriteLineBinding

class FavoriteViewHolder(
    private val binding: FavoriteLineBinding,
    private val onClick: (FavoriteModel) -> Unit,
    private val onRemove: (FavoriteModel) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(f: FavoriteModel) {
        binding.namePokemon.text = f.name.replaceFirstChar { it.uppercase() }
        SpriteLoader.load(binding.imagePokemon, f.imageUrl)
        binding.root.setOnClickListener { onClick(f) }
        binding.imageFavorite.setOnClickListener { onRemove(f) }
    }
}
