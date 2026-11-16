package com.example.pokedex.Ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.pokedex.R
import com.example.pokedex.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            binding.statusBarBackground.updateLayoutParams {
                height = systemBars.top.coerceAtLeast(1)
            }
            insets
        }

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace<SearchFragment>(R.id.fragment_container_view)
            }
        }

        when (supportFragmentManager.findFragmentById(R.id.fragment_container_view)) {
            is GridFragment -> paintGrid()
            is FavoritesFragment -> paintFavorite()
            else -> paintSearch()
        }

        binding.searchImage.setOnClickListener(this)
        binding.gridImage.setOnClickListener(this)
        binding.favImage.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        if (v?.id == binding.searchImage.id){
            paintSearch()
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace<SearchFragment>(R.id.fragment_container_view)
            }
        }

        if (v?.id == binding.gridImage.id){
            paintGrid()
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace<GridFragment>(R.id.fragment_container_view)
            }
        }

        if (v?.id == binding.favImage.id){
            paintFavorite()
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace<FavoritesFragment>(R.id.fragment_container_view)
            }
        }
    }

    private fun paintSearch(){
        val cor = ContextCompat.getColor(this, R.color.yellow)
        binding.searchImage.imageTintList = ColorStateList.valueOf(cor)
        binding.gridImage.imageTintList = null
        binding.favImage.imageTintList = null
    }

    private fun paintGrid(){
        val cor = ContextCompat.getColor(this, R.color.yellow)
        binding.searchImage.imageTintList = null
        binding.gridImage.imageTintList = ColorStateList.valueOf(cor)
        binding.favImage.imageTintList = null
    }

    private fun paintFavorite(){
        val cor = ContextCompat.getColor(this, R.color.yellow)
        binding.searchImage.imageTintList = null
        binding.gridImage.imageTintList = null
        binding.favImage.imageTintList = ColorStateList.valueOf(cor)
    }

}