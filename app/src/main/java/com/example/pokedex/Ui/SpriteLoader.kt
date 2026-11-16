package com.example.pokedex.Ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.pokedex.R

object SpriteLoader {

    private val options = RequestOptions()
        .override(Target.SIZE_ORIGINAL)
        .dontTransform()

    fun preload(context: Context, url: String?, onDone: () -> Unit) {
        if (url == null) {
            onDone(); return
        }
        Glide.with(context.applicationContext)
            .load(url)
            .apply(options)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?, model: Any?,
                    target: Target<Drawable>, isFirstResource: Boolean
                ): Boolean {
                    onDone(); return false
                }

                override fun onResourceReady(
                    resource: Drawable, model: Any,
                    target: Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean
                ): Boolean {
                    onDone(); return false
                }
            })
            .preload()
    }

    fun load(view: ImageView, url: String?) {
        Glide.with(view)
            .load(url)
            .apply(options)
            .placeholder(R.drawable.pokeball_pokemon_svgrepo_com)
            .error(R.drawable.pokeball_pokemon_svgrepo_com)
            .into(view)
    }
}
