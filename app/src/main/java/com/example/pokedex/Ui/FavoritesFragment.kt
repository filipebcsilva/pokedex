package com.example.pokedex.Ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokedex.Data.api.PokemonEntity
import com.example.pokedex.Data.api.PokemonService
import com.example.pokedex.Data.api.RetrofitClient
import com.example.pokedex.Data.local.FavoriteModel
import com.example.pokedex.Data.local.FavoritesRepository
import com.example.pokedex.R
import com.example.pokedex.Ui.Adapter.FavoritesAdapter
import com.example.pokedex.databinding.FragmentFavoritesBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoritesFragment : Fragment() {

    lateinit var binding: FragmentFavoritesBinding
    private lateinit var favorites: FavoritesRepository
    private val adapter = FavoritesAdapter(
        onClick = { openDetail(it) },
        onRemove = { remove(it) }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        favorites = FavoritesRepository(requireContext())

        binding.recyclerListFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerListFavorites.adapter = adapter

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadFavorites()
    }

    private fun loadFavorites() {
        adapter.submit(favorites.getAll())
    }

    private fun remove(f: FavoriteModel) {
        favorites.remove(f)
        loadFavorites()
    }

    private fun openDetail(f: FavoriteModel) {
        val service = RetrofitClient.createService(PokemonService::class.java)
        service.getPokemon(f.name).enqueue(object : Callback<PokemonEntity> {
            override fun onResponse(call: Call<PokemonEntity>, response: Response<PokemonEntity>) {
                val pokemon = response.body()
                if (!isAdded) return
                if (response.isSuccessful && pokemon != null) {
                    parentFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace(R.id.fragment_container_view, PokemonDetailFragment.newInstance(pokemon))
                        addToBackStack(null)
                    }
                } else {
                    Toast.makeText(requireContext(), "Pokémon não encontrado", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PokemonEntity>, t: Throwable) {
                if (isAdded) Toast.makeText(requireContext(), "Sem conexão", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
