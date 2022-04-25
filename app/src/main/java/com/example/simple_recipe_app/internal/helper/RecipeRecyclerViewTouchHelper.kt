package com.example.simple_recipe_app.internal.helper

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.simple_recipe_app.model.RecipeModel
import java.util.*

class RecipeRecyclerViewTouchHelper: ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP.or(ItemTouchHelper.DOWN), 0) {
    var items: List<RecipeModel> = listOf()

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ): Boolean {
        val start = viewHolder.adapterPosition
        val end = target.adapterPosition

        Collections.swap(items, start, end)
        recyclerView.adapter?.notifyItemMoved(start, end)

        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
}