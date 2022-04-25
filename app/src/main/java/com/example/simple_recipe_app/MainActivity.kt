package com.example.simple_recipe_app

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simple_recipe_app.activity.DetailActivity
import com.example.simple_recipe_app.view_model.RecipeViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var recipeViewModel: RecipeViewModel? = null
    private val recursiveBackupFromAPIThread = object: Thread() {
        val refresh = 1000
        var isSyncAllowed: Boolean = true
        var isRecurring: Boolean = true

        override fun run() {
            super.run()

            var min = 5
            var sec = 0

            while (isRecurring) {
                Thread.sleep(refresh.toLong())
                Handler(Looper.getMainLooper()).post {
                    if (min == 0 && sec == 0) {
                        if (isSyncAllowed) {
                            onBackupFromAPI()
                        }
                        min = 5
                        sec = 0
                    }

                    if (sec == 0) {
                        min -= 1
                        sec = 60
                    }

                    sec -= 1

                    if (sec < 10) {
                        tv_timer.text = "Backup in ${min}:0${sec}"
                    } else {
                        tv_timer.text = "Backup in ${min}:${sec}"
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv_recipes.layoutManager = LinearLayoutManager(this)
        supportActionBar?.title = "All Recipe"

        recipeViewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)
        recipeViewModel?.let { viewModel ->
            ItemTouchHelper(viewModel.recipeRecyclerViewTouchHelper).attachToRecyclerView(rv_recipes)
            viewModel.recipeRecyclerViewAdapter.setOnClickItem {
                val next = Intent(this, DetailActivity::class.java)

                next.putExtra("id", it.id)
                next.putExtra("name", it.name)
                next.putExtra("headline", it.headline)
                next.putExtra("description", it.description)
                next.putExtra("difficulty", it.difficulty)
                next.putExtra("time", it.time)
                next.putExtra("calories", it.calories)
                next.putExtra("carbos", it.carbos)
                next.putExtra("fats", it.fats)
                next.putExtra("proteins", it.proteins)
                next.putExtra("thumb", it.thumb)
                next.putExtra("image", it.image)
                next.putExtra("favorite", it.favorite)

                startActivity(next)
            }
        }

        sw_refresh.isRefreshing = true
        sw_refresh.setOnRefreshListener {
            onBackupFromAPI()
        }

        recursiveBackupFromAPIThread.isRecurring = true
        recursiveBackupFromAPIThread.start()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.im_sync -> {
                if (!isInternetConnection(this)) {
                    item.setIcon(R.drawable.ic_sync_problem)
                    Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show()
                } else {
                    recursiveBackupFromAPIThread.isSyncAllowed = !recursiveBackupFromAPIThread.isSyncAllowed
                    if (recursiveBackupFromAPIThread.isSyncAllowed) {
                        tv_timer.visibility = View.VISIBLE
                        item.setIcon(R.drawable.ic_sync)
                        Toast.makeText(this, "On backup every 5 minutes", Toast.LENGTH_SHORT).show()
                    } else {
                        tv_timer.visibility = View.GONE
                        item.setIcon(R.drawable.ic_sync_disabled)
                        Toast.makeText(this, "Off backup", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            R.id.im_sort -> {
                val imSort = findViewById<View>(R.id.im_sort)
                val popupMenuItem: PopupMenu = PopupMenu(this, imSort)

                popupMenuItem.setOnMenuItemClickListener { itemMenu ->
                    when (itemMenu.itemId) {
                        R.id.im_sortByName -> {
                            recipeViewModel?.let { viewModel ->
                                viewModel.setItems(viewModel.getItems().sortedBy {
                                    it.name
                                })
                                rv_recipes.adapter = viewModel.recipeRecyclerViewAdapter
                                Toast.makeText(this, "Sort by name", Toast.LENGTH_SHORT).show()
                            }
                            true
                        }
                        R.id.im_sortByCalories -> {
                            recipeViewModel?.let { viewModel ->
                                viewModel.setItems(viewModel.getItems().sortedBy {
                                    it.calories
                                })
                                rv_recipes.adapter = viewModel.recipeRecyclerViewAdapter
                                Toast.makeText(this, "Sort by calories", Toast.LENGTH_SHORT).show()
                            }
                            true
                        }
                        R.id.im_sortByProteins -> {
                            recipeViewModel?.let { viewModel ->
                                viewModel.setItems(viewModel.getItems().sortedBy {
                                    it.proteins
                                })
                                rv_recipes.adapter = viewModel.recipeRecyclerViewAdapter
                                Toast.makeText(this, "Sort by proteins", Toast.LENGTH_SHORT).show()
                            }
                            true
                        }
                        R.id.im_sortByFavorite -> {
                            recipeViewModel?.let { viewModel ->
                                viewModel.setItems(viewModel.getItems().sortedByDescending {
                                    it.favorite
                                })
                                rv_recipes.adapter = viewModel.recipeRecyclerViewAdapter
                                Toast.makeText(this, "Sort by favorite", Toast.LENGTH_SHORT).show()
                            }
                            true
                        }
                        else -> false
                    }
                }
                popupMenuItem.menuInflater.inflate(R.menu.sort_item_menu, popupMenuItem.menu)
                popupMenuItem.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        onFirstLaunch()
    }

    override fun onStop() {
        super.onStop()
        onChangedFromLocalDB()
    }

    private fun onChangedFromLocalDB() {
        recipeViewModel?.let { viewModel ->
            viewModel.changeFromLocalDB(viewModel.getItems()) {
                sw_refresh.isRefreshing = false
            }
        }
    }

    private fun onFirstLaunch() {
        recipeViewModel?.let { viewModel ->
            viewModel.readFromLocalDB().observe(this) {
                if (it.isEmpty()) {
                    viewModel.readFromAPI().observe(this) { raw ->
                        viewModel.addFromLocalDB(raw) {}
                    }
                }

                viewModel.setItems(it)
                rv_recipes.adapter = viewModel.recipeRecyclerViewAdapter
                supportActionBar?.title = "All Recipe (${it.size})"
                sw_refresh.isRefreshing = false
            }
        }
    }

    private fun onBackupFromAPI() {
        if (!isInternetConnection(this)) {
            sw_refresh.isRefreshing = false
            Log.i("onBackupFromAPI", "Cannot receive any data because the internet connection failed.")
            return
        }

        recipeViewModel?.let { viewModel ->
            viewModel.readFromAPI().observe(this) {
                viewModel.mergeFromLocalDB(viewModel.getItems(), it) {
                    sw_refresh.isRefreshing = false
                    Log.i("onBackupFromAPI", "Receive and merge data successful.")
                }
            }
        }
    }

    private fun isInternetConnection(activity: AppCompatActivity):Boolean{
        val connectivityManager=activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo=connectivityManager.activeNetworkInfo
        return  networkInfo!=null && networkInfo.isConnected
    }
}