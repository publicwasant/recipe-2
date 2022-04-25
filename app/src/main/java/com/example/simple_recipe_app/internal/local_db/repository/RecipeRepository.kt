package com.example.simple_recipe_app.internal.local_db.repository

import androidx.lifecycle.LiveData
import com.example.simple_recipe_app.internal.local_db.dao.RecipeDAO
import com.example.simple_recipe_app.model.RecipeModel

class RecipeRepository(private val recipeDAO: RecipeDAO) {
    fun read(): LiveData<List<RecipeModel>> = recipeDAO.read()
    fun readById(id: String): LiveData<RecipeModel> = recipeDAO.readById(id)

    suspend fun add(items: List<RecipeModel>){
        recipeDAO.add(items)
    }

    suspend fun addAndReplace(items: List<RecipeModel>){
        recipeDAO.addAndReplace(items)
    }

    suspend fun edit(item: RecipeModel){
        recipeDAO.edit(item)
    }

    suspend fun delete() {
        recipeDAO.delete()
    }
}