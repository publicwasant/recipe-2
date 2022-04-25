package com.example.simple_recipe_app.internal.network

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RecipeInstanceRetrofit {
    companion object {
        fun get(): Retrofit {
            return Retrofit.Builder()
                .baseUrl("https://hf-android-app.s3-eu-west-1.amazonaws.com/android-test/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        }
    }
}