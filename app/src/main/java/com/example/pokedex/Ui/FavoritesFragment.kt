package com.example.pokedex.Ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokedex.Data.remote.FavoriteModel
import com.example.pokedex.R
import com.example.pokedex.Ui.Adapter.FavoritesAdapter
import com.example.pokedex.ViewModel.FavoritesViewModel
import com.example.pokedex.databinding.FragmentFavoritesBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class FavoritesFragment : Fragment() {

    lateinit var binding: FragmentFavoritesBinding
    private lateinit var viewModel: FavoritesViewModel
    private val adapter = FavoritesAdapter(
        onClick = { viewModel.abrirDetalhe(it) },
        onRemove = { confirmarRemocao(it) }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(FavoritesViewModel::class.java)

        binding.recyclerListFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerListFavorites.adapter = adapter
        binding.logoutButton.setOnClickListener { sair() }

        setObservers()

        return binding.root
    }

    private fun setObservers() {
        viewModel.favoritos.observe(viewLifecycleOwner) { adapter.submit(it) }

        viewModel.mensagem.observe(viewLifecycleOwner) { msg ->
            if (msg != null) {
                Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
                viewModel.mensagemExibida()
            }
        }

        viewModel.selecionado.observe(viewLifecycleOwner) { pokemon ->
            if (pokemon != null) {
                viewModel.limparSelecao()
                val navController = findNavController()
                if (navController.currentDestination?.id == R.id.favoritesFragment) {
                    navController.navigate(
                        R.id.action_favoritesFragment_to_pokemonDetailFragment,
                        PokemonDetailFragment.args(pokemon)
                    )
                }
            }
        }
    }

    private fun confirmarRemocao(f: FavoriteModel) {
        val nome = f.name.replaceFirstChar { it.uppercase() }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.remove_dialog_title)
            .setMessage(getString(R.string.remove_dialog_message, nome))
            .setPositiveButton(R.string.dialog_yes) { _, _ -> viewModel.remover(f) }
            .setNegativeButton(R.string.dialog_no, null)
            .setNeutralButton(R.string.dialog_view_details) { _, _ -> viewModel.abrirDetalhe(f) }
            .show()
    }

    private fun sair() {
        viewModel.sair()
        val navController = findNavController()
        navController.navigate(R.id.action_global_loginFragment)
        navController.clearBackStack(R.id.favoritesFragment)
        navController.clearBackStack(R.id.gridFragment)
    }
}
