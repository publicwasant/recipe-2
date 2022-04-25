package com.example.simple_recipe_app.internal.network

import com.example.simple_recipe_app.model.RecipeModel
import io.reactivex.Observable
import retrofit2.http.GET

interface RecipeServiceRetrofit {
    @GET("recipes.json")
    fun read(): Observable<List<RecipeModel>>
}