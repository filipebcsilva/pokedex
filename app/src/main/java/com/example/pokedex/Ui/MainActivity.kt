package com.example.pokedex.Ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.pokedex.Data.remote.AuthRepository
import com.example.pokedex.R
import com.example.pokedex.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var tecladoAberto = false

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        val navHost = supportFragmentManager
            .findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navController = navHost.navController

        binding.bottomNav.setupWithNavController(navController)

        binding.bottomNav.setOnItemReselectedListener { item ->
            navController.popBackStack(item.itemId, false)
        }

        navController.addOnDestinationChangedListener { _, _, _ ->
            val mostrar = barraDeveAparecer()
            if (binding.bottomNav.isVisible != mostrar) {
                binding.bottomNav.isVisible = mostrar
                ViewCompat.requestApplyInsets(binding.main)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            tecladoAberto = insets.isVisible(WindowInsetsCompat.Type.ime())

            binding.statusBarBackground.updateLayoutParams {
                height = systemBars.top.coerceAtLeast(1)
            }

            binding.bottomNav.isVisible = barraDeveAparecer()
            val bottom = when {
                tecladoAberto -> ime.bottom
                binding.bottomNav.isVisible -> 0
                else -> systemBars.bottom
            }
            v.setPadding(systemBars.left, 0, systemBars.right, bottom)
            insets
        }

        if (savedInstanceState == null && !AuthRepository().isLoggedIn) {
            navController.navigate(R.id.action_global_loginFragment)
        }
    }

    private fun barraDeveAparecer(): Boolean =
        navController.currentDestination?.id in TOP_LEVEL_DESTINATIONS && !tecladoAberto

    companion object {
        private val TOP_LEVEL_DESTINATIONS = setOf(
            R.id.searchFragment,
            R.id.favoritesFragment,
            R.id.gridFragment
        )
    }
}
