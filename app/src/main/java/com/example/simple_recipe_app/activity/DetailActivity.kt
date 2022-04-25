package com.example.simple_recipe_app.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.simple_recipe_app.R
import com.example.simple_recipe_app.model.RecipeModel
import com.example.simple_recipe_app.view_model.RecipeViewModel
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {
    private var recipeViewModel: RecipeViewModel? = null
    private var item: RecipeModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        recipeViewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)
        item = RecipeModel(
            intent.getStringExtra("id").toString(),
            intent.getStringExtra("name").toString(),
            intent.getStringExtra("headline").toString(),
            intent.getStringExtra("description").toString(),
            intent.getStringExtra("time").toString(),
            intent.getIntExtra("difficulty", 0).toInt(),
            intent.getStringExtra("calories").toString(),
            intent.getStringExtra("carbos").toString(),
            intent.getStringExtra("fats").toString(),
            intent.getStringExtra("proteins").toString(),
            intent.getStringExtra("thumb").toString(),
            intent.getStringExtra("image").toString(),
            intent.getBooleanExtra("favorite", false)
        )

        item?.let {
            Glide.with(this)
                .load(it.image)
                .optionalFitCenter()
                .into(this.iv_detailImage)

            tv_detailNameAndHeadline.text = "${it.name} ${it.headline}"
            tv_detailDescription.text = "${it.description}"
            tv_detailValues1.text = "◦ Difficulty: ${it.difficulty} | Time: ${it.time}"
            tv_detailValues2.text = "◦ Calories: ${it.calories} | Carbos: ${it.carbos}"
            tv_detailValues3.text = "◦ Fats: ${it.fats} | Proteins: ${it.proteins}"
        }
        bt_favorite.setOnClickListener {
            item?.let {
                when (it.favorite) {
                    true -> {
                        it.favorite = !it.favorite
                        recipeViewModel?.editFromLocalDB(it) {}
                        Toast.makeText(this, "Remove Favorite", Toast.LENGTH_SHORT).show()
                    }
                    false -> {
                        it.favorite = !it.favorite
                        recipeViewModel?.editFromLocalDB(it) {}
                        Toast.makeText(this, "Add Favorite", Toast.LENGTH_SHORT).show()
                    }
                }
                setFavorite()
            }
        }

        setFavorite()
    }

    private fun setFavorite() {
        item?.let {
            when(it.favorite) {
                true -> bt_favorite.text = "❤ Favorite"
                false -> bt_favorite.text = "Add Favorite"
            }
        }
    }
}