package com.example.simple_recipe_app.internal.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.simple_recipe_app.R
import com.example.simple_recipe_app.model.RecipeModel
import kotlinx.android.synthetic.main.view_recycler_recipe.view.*

class RecipeRecyclerViewAdapter(
    private val appContext: Context
): RecyclerView.Adapter<RecipeRecyclerViewAdapter.ViewHolder>() {
    class ViewHolder(
        private val view: View
    ): RecyclerView.ViewHolder(view) {
        val cvRecipe: CardView = view.cv_recipe
        val ivThumb: ImageView = view.iv_thumb
        val tvNameAndHeadline: TextView = view.tv_nameAndHeadline
        val tvValues: TextView = view.tv_values
        val tvHint: TextView = view.tv_hint
    }

    private var onClickItem: ((RecipeModel) -> Unit)? = null
    var items: List<RecipeModel> = listOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        return ViewHolder(LayoutInflater
            .from(appContext)
            .inflate(R.layout.view_recycler_recipe, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        if (item.calories == "") {
            item.calories = "-"
        }

        if (item.proteins == "") {
            item.proteins = "-"
        }

        Glide.with(appContext)
            .load(item.thumb)
            .centerCrop()
            .into(holder.ivThumb)

        holder.tvNameAndHeadline.text = "${item.name} ${item.headline}"
        holder.tvValues.text = "Calories: ${item.calories} | Proteins: ${item.proteins}"

        if (item.favorite) {
            holder.tvHint.text = "❤ Favorite | " + holder.tvHint.text
        }

        holder.cvRecipe.setOnClickListener {
            onClickItem?.let {
                it(item)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun setOnClickItem(then: (RecipeModel) -> Unit) {
        onClickItem = then
    }
}