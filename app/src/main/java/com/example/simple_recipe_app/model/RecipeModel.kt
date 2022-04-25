package com.example.simple_recipe_app.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeModel(
    @PrimaryKey
    var id: String,
    var name: String,
    var headline: String,
    var description: String,
    var time: String,
    var difficulty: Int,
    var calories: String,
    var carbos: String,
    var fats: String,
    var proteins: String,
    var thumb: String,
    var image: String,
    var favorite: Boolean
)
