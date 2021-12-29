package com.alansnaki.samplemap.ui.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.alansnaki.samplemap.R
import com.alansnaki.samplemap.databinding.GridItemLayoutBinding

class ViewBuilder {

    companion object {
        /** Función para generar los item del grid.
         *
         * @Deprecated Errores, ya que debería establecerse el parent*.
         */
        @Deprecated(
            "Implementations issues", ReplaceWith(
                "addItemToGrid(c:Context, grid: GridLayout)",
                "android.view.LayoutInflater",
                "com.alansnaki.samplemap.R"
            )
        )
        fun gridItem(context: Context): View {

            return LayoutInflater.from(context).inflate(
                R.layout.grid_item_layout,
                null,
                false
            )
        }

        /** Función para generar items del grid.
         */
        private fun addItemToGrid(c: Context, grid: GridLayout): View {
            return LayoutInflater.from(c).inflate(
                R.layout.grid_item_layout,
                grid,
                false
            )
        }

        fun addItemToGrid(
            c: Context,
            grid: GridLayout,
            @DrawableRes img: Int?,
            @ColorRes color: Int?,
            label: String?
        ): View {
            val view = addItemToGrid(c, grid)
            val bind = GridItemLayoutBinding.bind(view)
            bind.apply {
                img?.let {
                    image.setImageDrawable(ContextCompat.getDrawable(c, it))
                } ?: image.setImageDrawable(ContextCompat.getDrawable(c, R.drawable.ic_box_24))

                color?.let {
                    image.imageTintList = ContextCompat.getColorStateList(c, it)
                }
                if (color == null)
                    image.imageTintList = ContextCompat.getColorStateList(c, R.color.di_blue)

                label?.let {
                    text.text = label
                } ?: text.setText("Item")
            }

            return view
        }
    }


}