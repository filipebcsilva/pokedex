package com.example.pokedex.Ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokedex.R
import com.example.pokedex.Ui.Adapter.GridAdapter
import com.example.pokedex.ViewModel.GridViewModel
import com.example.pokedex.databinding.FragmentGridBinding

class GridFragment : Fragment() {
    lateinit var binding: FragmentGridBinding
    lateinit var viewModel: GridViewModel
    private lateinit var adapter: GridAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGridBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(GridViewModel::class.java)

        adapter = GridAdapter { item -> viewModel.selecionar(item) }
        val layoutManager = GridLayoutManager(requireContext(), SPAN_COUNT)
        binding.recyclerGridPokemons.layoutManager = layoutManager
        binding.recyclerGridPokemons.adapter = adapter

        binding.recyclerGridPokemons.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy <= 0) return
                val ultimoVisivel = layoutManager.findLastVisibleItemPosition()
                if (ultimoVisivel >= adapter.itemCount - LOAD_THRESHOLD) {
                    viewModel.carregarProximaPagina()
                }
            }
        })

        setObservers()

        if (viewModel.pokemons.value.isNullOrEmpty()) viewModel.carregarProximaPagina()

        return binding.root
    }

    private fun setObservers() {
        viewModel.pokemons.observe(viewLifecycleOwner) { lista ->
            adapter.submit(lista)
        }

        viewModel.carregando.observe(viewLifecycleOwner) { carregando ->
            binding.progressGrid.visibility = if (carregando) View.VISIBLE else View.GONE
        }

        viewModel.erro.observe(viewLifecycleOwner) { msg ->
            if (msg != null) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                viewModel.limparErro()
            }
        }

        viewModel.selecionado.observe(viewLifecycleOwner) { pokemon ->
            if (pokemon != null) {
                viewModel.limparSelecao()
                val navController = findNavController()
                if (navController.currentDestination?.id == R.id.gridFragment) {
                    navController.navigate(
                        R.id.action_gridFragment_to_pokemonDetailFragment,
                        PokemonDetailFragment.args(pokemon)
                    )
                }
            }
        }
    }

    companion object {
        private const val SPAN_COUNT = 3

        private const val LOAD_THRESHOLD = 9
    }
}
