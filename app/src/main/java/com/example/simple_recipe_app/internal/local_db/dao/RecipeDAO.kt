package com.example.simple_recipe_app.internal.local_db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.simple_recipe_app.model.RecipeModel

@Dao
interface RecipeDAO {
    /*
    * Recipe Data Access Object
    */

    @Query("SELECT * FROM recipes")
    fun read(): LiveData<List<RecipeModel>>

    @Query("SELECT * FROM recipes where id=:id")
    fun readById(id: String): LiveData<RecipeModel>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun add(items: List<RecipeModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAndReplace(items: List<RecipeModel>)

    @Update()
    fun edit(item: RecipeModel)

    @Query("DELETE FROM recipes")
    fun delete()
}