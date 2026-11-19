package com.example.pokedex.Ui

import SearchViewModel
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.pokedex.Data.api.PokemonEntity
import com.example.pokedex.R
import com.example.pokedex.databinding.FragmentSearchBinding

class SearchFragment : Fragment(), View.OnClickListener {

    lateinit var binding: FragmentSearchBinding
    lateinit var viewModel: SearchViewModel

    private val handler = Handler(Looper.getMainLooper())
    private var pendingNavigation: Runnable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

        binding.searchButton.setOnClickListener(this)
        setObservers()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cancelPendingNavigation()
    }

    override fun onClick(v: View?) {
        if (v?.id == binding.searchButton.id) {
            val nome = binding.editTextPokemon.text.toString()
            if (nome.isNotBlank()) {
                setLoading(true)
                viewModel.buscar(nome)
            }
        }
    }

    private fun setObservers() {
        viewModel.pokemon.observe(viewLifecycleOwner) { pokemon ->
            if (pokemon != null) {
                viewModel.limpar()
                waitSpriteThenOpen(pokemon)
            }
        }

        viewModel.erro.observe(viewLifecycleOwner) { msg ->
            if (msg != null) {
                setLoading(false)
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun waitSpriteThenOpen(pokemon: PokemonEntity) {
        cancelPendingNavigation()
        val open = Runnable { openDetail(pokemon) }
        pendingNavigation = open

        handler.postDelayed(open, MAX_WAIT_MS)
        SpriteLoader.preload(requireContext(), pokemon.imageUrl) {
            if (pendingNavigation === open) {
                handler.removeCallbacks(open)
                handler.post(open)
            }
        }
        if (pokemon.sprites.frontFemale != null) {
            SpriteLoader.preload(requireContext(), pokemon.femaleImageUrl) {}
        }
    }

    private fun openDetail(pokemon: PokemonEntity) {
        pendingNavigation = null
        if (!isAdded || !lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) return
        setLoading(false)
        val navController = findNavController()
        if (navController.currentDestination?.id != R.id.searchFragment) return
        navController.navigate(
            R.id.action_searchFragment_to_pokemonDetailFragment,
            PokemonDetailFragment.args(pokemon)
        )
    }

    private fun cancelPendingNavigation() {
        pendingNavigation?.let { handler.removeCallbacks(it) }
        pendingNavigation = null
    }

    private fun setLoading(loading: Boolean) {
        binding.searchButton.isEnabled = !loading
        binding.searchButton.setText(if (loading) R.string.loading else R.string.search_button)
    }

    companion object {
        private const val MAX_WAIT_MS = 3000L
    }
}
