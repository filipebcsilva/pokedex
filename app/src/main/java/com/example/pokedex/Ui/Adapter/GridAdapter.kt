package com.example.pokedex.Ui.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pokedex.Data.api.PokemonItem
import com.example.pokedex.Ui.ViewHolder.GridViewHolder
import com.example.pokedex.databinding.GridItemBinding

class GridAdapter(
    private val onClick: (PokemonItem) -> Unit
) : RecyclerView.Adapter<GridViewHolder>() {

    private var list: List<PokemonItem> = emptyList()

    fun submit(newList: List<PokemonItem>) {
        val oldSize = list.size
        list = newList
        if (newList.size > oldSize) notifyItemRangeInserted(oldSize, newList.size - oldSize)
        else notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val binding = GridItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GridViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size
}
