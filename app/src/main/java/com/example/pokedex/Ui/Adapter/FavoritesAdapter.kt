package com.example.pokedex.Ui.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pokedex.Data.remote.FavoriteModel
import com.example.pokedex.Ui.ViewHolder.FavoriteViewHolder
import com.example.pokedex.databinding.FavoriteLineBinding

class FavoritesAdapter(
    private val onClick: (FavoriteModel) -> Unit,
    private val onRemove: (FavoriteModel) -> Unit
) : RecyclerView.Adapter<FavoriteViewHolder>() {

    private var list: List<FavoriteModel> = emptyList()

    fun submit(newList: List<FavoriteModel>) {
        list = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = FavoriteLineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding, onClick, onRemove)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size
}
