package com.example.simple_recipe_app.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.example.simple_recipe_app.internal.adapter.RecipeRecyclerViewAdapter
import com.example.simple_recipe_app.internal.helper.RecipeRecyclerViewTouchHelper
import com.example.simple_recipe_app.internal.local_db.database.RecipeDatabase
import com.example.simple_recipe_app.internal.local_db.repository.RecipeRepository
import com.example.simple_recipe_app.internal.network.RecipeInstanceRetrofit
import com.example.simple_recipe_app.internal.network.RecipeServiceRetrofit
import com.example.simple_recipe_app.model.RecipeModel
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class RecipeViewModel(application: Application): AndroidViewModel(application) {
    private val recipeServiceRetrofit: RecipeServiceRetrofit = RecipeInstanceRetrofit.get().create(RecipeServiceRetrofit::class.java)
    private var recipeRepository: RecipeRepository = RecipeRepository(RecipeDatabase.getDatabase(application).recipeDao())

    private var items: List<RecipeModel> = listOf()

    val recipeRecyclerViewAdapter: RecipeRecyclerViewAdapter = RecipeRecyclerViewAdapter(application)
    val recipeRecyclerViewTouchHelper: RecipeRecyclerViewTouchHelper = RecipeRecyclerViewTouchHelper()

    fun setItems(items: List<RecipeModel>) {
        this.items = items
        recipeRecyclerViewAdapter.items = items
        recipeRecyclerViewTouchHelper.items = items
    }

    fun getItems(): List<RecipeModel> {
        return items
    }

    private fun merge(origin: List<RecipeModel>, new: List<RecipeModel>): List<RecipeModel> {
        if (origin.isEmpty()) {
            return new
        }

        if (new.isEmpty()) {
            return origin
        }

        val temp: MutableList<RecipeModel> = origin.toMutableList()

        for (item in new) {
            val conflictInd = temp.indexOfFirst { it.id == item.id }

            if (conflictInd != -1) {
                val fav = temp[conflictInd].favorite

                temp[conflictInd] = item
                temp[conflictInd].favorite = fav
            } else {
                temp.add(item)
            }
        }

        return temp.toList()
    }

    fun readFromLocalDB(): LiveData<List<RecipeModel>> = recipeRepository.read()

    fun addFromLocalDB(items: List<RecipeModel>, then: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeRepository.add(items)
            then()
        }
    }

    fun changeFromLocalDB(items: List<RecipeModel>, then: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeRepository.addAndReplace(items)
            then()
        }
    }

    fun mergeFromLocalDB(origin: List<RecipeModel>, new: List<RecipeModel>, then: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeRepository.addAndReplace(merge(origin, new))
            then()
        }
    }

    fun editFromLocalDB(item: RecipeModel, then: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeRepository.edit(item)
            then()
        }
    }

    fun deleteFromLocalDB(then: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeRepository.delete()
            then()
        }
    }

    fun readFromAPI(): MutableLiveData<List<RecipeModel>> {
        val result: MutableLiveData<List<RecipeModel>> = MutableLiveData()
        recipeServiceRetrofit.read()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<RecipeModel>> {
                override fun onComplete() {}

                override fun onError(e: Throwable) {
                    result.postValue(null)
                }

                override fun onNext(t: List<RecipeModel>) {
                    result.postValue(t)
                }

                override fun onSubscribe(d: Disposable) {}
            })
        return result
    }
}