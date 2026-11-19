package com.example.pokedex.Ui

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.os.BundleCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.pokedex.Data.api.PokemonEntity
import com.example.pokedex.R
import com.example.pokedex.ViewModel.DetailViewModel
import com.example.pokedex.databinding.FragmentPokemonDetailBinding
import com.example.pokedex.databinding.ItemStatBinding
import com.google.android.material.snackbar.Snackbar

class PokemonDetailFragment : Fragment() {

    private var _binding: FragmentPokemonDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var pokemon: PokemonEntity
    private lateinit var viewModel: DetailViewModel
    private var isFemale = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pokemon = BundleCompat.getSerializable(
            requireArguments(), ARG_POKEMON, PokemonEntity::class.java
        ) ?: error("PokemonDetailFragment precisa de um Pokémon")
        isFemale = savedInstanceState?.getBoolean(STATE_FEMALE) ?: false
        viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)
        viewModel.iniciar(pokemon)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPokemonDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener { findNavController().navigateUp() }

        binding.favoriteButton.setOnClickListener { toggleFavorite() }
        viewModel.favorito.observe(viewLifecycleOwner) { renderFavorite(it) }
        viewModel.erro.observe(viewLifecycleOwner) { erro ->
            if (erro) {
                Snackbar.make(binding.root, R.string.error_generic, Snackbar.LENGTH_SHORT).show()
                viewModel.erroExibido()
            }
        }

        binding.maleButton.setOnClickListener { selectGender(female = false) }
        binding.femaleButton.setOnClickListener { selectGender(female = true) }

        binding.imagePokemon.contentDescription = pokemon.name.replaceFirstChar { it.uppercase() }

        renderTypes()
        renderGender()
        renderStats()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(STATE_FEMALE, isFemale)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun toggleFavorite() {
        val agoraFavorito = viewModel.alternar()
        val nome = pokemon.name.replaceFirstChar { it.uppercase() }
        val texto = getString(
            if (agoraFavorito) R.string.favorite_added else R.string.favorite_removed, nome
        )
        Snackbar.make(binding.root, texto, Snackbar.LENGTH_LONG)
            .setAction(R.string.undo) { viewModel.desfazer(eraFavorito = !agoraFavorito) }
            .show()
    }

    private fun renderFavorite(fav: Boolean) {
        binding.favoriteButton.setImageResource(
            if (fav) R.drawable.heart_svgrepo_com else R.drawable.ic_heart_outline
        )
        binding.favoriteButton.imageTintList = if (fav) {
            ColorStateList.valueOf(color(R.color.red))
        } else {
            val attrs = requireContext().obtainStyledAttributes(intArrayOf(android.R.attr.textColorPrimary))
            attrs.getColorStateList(0).also { attrs.recycle() }
        }
    }

    private fun renderTypes() {
        val container = binding.typesContainer
        container.removeAllViews()
        val dp = resources.displayMetrics.density

        pokemon.typeNames.forEachIndexed { index, type ->
            val info = TYPE_INFO[type]
            val badge = TextView(requireContext()).apply {
                text = info?.let { getString(it.label) } ?: type.replaceFirstChar { it.uppercase() }
                setTextColor(color(R.color.white))
                textSize = 13f
                setTypeface(typeface, Typeface.BOLD)
                setPadding((10 * dp).toInt(), (3 * dp).toInt(), (10 * dp).toInt(), (3 * dp).toInt())
                setBackgroundResource(R.drawable.bg_rounded)
                backgroundTintList = ColorStateList.valueOf(color(info?.color ?: R.color.type_normal))
            }
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { if (index > 0) marginStart = (12 * dp).toInt() }
            container.addView(badge, params)
        }
    }

    private fun selectGender(female: Boolean) {
        if (isFemale == female) return
        isFemale = female
        renderGender()
    }

    private fun renderGender() {
        styleGenderButton(binding.maleButton, selected = !isFemale, R.color.gender_male)
        styleGenderButton(binding.femaleButton, selected = isFemale, R.color.gender_female)

        val url = if (isFemale) pokemon.femaleImageUrl else pokemon.imageUrl
        SpriteLoader.load(binding.imagePokemon, url)
    }

    private fun styleGenderButton(view: TextView, selected: Boolean, @ColorRes accent: Int) {
        if (selected) {
            view.setBackgroundResource(R.drawable.bg_rounded)
            view.backgroundTintList = ColorStateList.valueOf(color(accent))
            view.setTextColor(color(R.color.white))
        } else {
            view.background = null
            view.backgroundTintList = null
            view.setTextColor(color(accent))
        }
        view.isSelected = selected
    }

    private fun renderStats() {
        val stats = pokemon.statMap
        bindStat(binding.statHp, R.string.stat_hp, stats["hp"], R.color.stat_hp_bg, R.color.stat_hp_bar)
        bindStat(binding.statAttack, R.string.stat_attack, stats["attack"], R.color.stat_attack_bg, R.color.stat_attack_bar)
        bindStat(binding.statDefense, R.string.stat_defense, stats["defense"], R.color.stat_defense_bg, R.color.stat_defense_bar)
        bindStat(binding.statSpAttack, R.string.stat_sp_attack, stats["special-attack"], R.color.stat_sp_attack_bg, R.color.stat_sp_attack_bar)
        bindStat(binding.statSpDefense, R.string.stat_sp_defense, stats["special-defense"], R.color.stat_sp_defense_bg, R.color.stat_sp_defense_bar)
        bindStat(binding.statSpeed, R.string.stat_speed, stats["speed"], R.color.stat_speed_bg, R.color.stat_speed_bar)
    }

    private fun bindStat(
        row: ItemStatBinding,
        @StringRes label: Int,
        value: Int?,
        @ColorRes background: Int,
        @ColorRes bar: Int
    ) {
        val v = value ?: 0
        row.root.setBackgroundColor(color(background))
        row.statLabel.setText(label)
        row.statValue.text = v.toString()
        row.statBar.progress = v
        row.statBar.progressTintList = ColorStateList.valueOf(color(bar))
    }

    private fun color(@ColorRes res: Int) = ContextCompat.getColor(requireContext(), res)

    private data class TypeStyle(@StringRes val label: Int, @ColorRes val color: Int)

    companion object {
        private const val ARG_POKEMON = "pokemon"
        private const val STATE_FEMALE = "state_female"

        private val TYPE_INFO = mapOf(
            "normal" to TypeStyle(R.string.type_normal, R.color.type_normal),
            "fire" to TypeStyle(R.string.type_fire, R.color.type_fire),
            "water" to TypeStyle(R.string.type_water, R.color.type_water),
            "grass" to TypeStyle(R.string.type_grass, R.color.type_grass),
            "electric" to TypeStyle(R.string.type_electric, R.color.type_electric),
            "ice" to TypeStyle(R.string.type_ice, R.color.type_ice),
            "fighting" to TypeStyle(R.string.type_fighting, R.color.type_fighting),
            "poison" to TypeStyle(R.string.type_poison, R.color.type_poison),
            "ground" to TypeStyle(R.string.type_ground, R.color.type_ground),
            "flying" to TypeStyle(R.string.type_flying, R.color.type_flying),
            "psychic" to TypeStyle(R.string.type_psychic, R.color.type_psychic),
            "bug" to TypeStyle(R.string.type_bug, R.color.type_bug),
            "rock" to TypeStyle(R.string.type_rock, R.color.type_rock),
            "ghost" to TypeStyle(R.string.type_ghost, R.color.type_ghost),
            "dragon" to TypeStyle(R.string.type_dragon, R.color.type_dragon),
            "dark" to TypeStyle(R.string.type_dark, R.color.type_dark),
            "steel" to TypeStyle(R.string.type_steel, R.color.type_steel),
            "fairy" to TypeStyle(R.string.type_fairy, R.color.type_fairy),
        )

        fun args(pokemon: PokemonEntity) = Bundle().apply { putSerializable(ARG_POKEMON, pokemon) }
    }
}
