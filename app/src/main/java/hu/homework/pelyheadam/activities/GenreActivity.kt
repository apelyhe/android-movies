package hu.homework.pelyheadam.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.recyclerview.widget.GridLayoutManager
import hu.homework.pelyheadam.R
import hu.homework.pelyheadam.adapter.GenreAdapter
import hu.homework.pelyheadam.databinding.ActivityGenreBinding
import hu.homework.pelyheadam.entities.Genre
import hu.homework.pelyheadam.interfaces.OnGenreSelectedListener

class GenreActivity : AppCompatActivity(), OnGenreSelectedListener {

    private lateinit var binding: ActivityGenreBinding
    private lateinit var adapter : GenreAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setTitle("Kategóriák")
        initBottomMenu()
        initRecycleView()
    }

    private fun initBottomMenu() {
        binding.bottomNavigationView.menu.getItem(2).isChecked = true
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.popular -> {
                    val recommendationIntent = Intent()
                    recommendationIntent.setClass(this@GenreActivity, PopularActivity::class.java)
                    //recommendationIntent.putExtra(RecommendedActivity.EXTRA_CITY_NAME, city)
                    startActivity(recommendationIntent)
                }
                R.id.recommendation -> {
                    val recommendationIntent = Intent()
                    recommendationIntent.setClass(this@GenreActivity, RecommendedActivity::class.java)
                    //recommendationIntent.putExtra(RecommendedActivity.EXTRA_CITY_NAME, city)
                    startActivity(recommendationIntent)
                }
                R.id.categories -> {
                    val categoriesIntent = Intent()
                    categoriesIntent.setClass(this@GenreActivity, GenreActivity::class.java)
                    //recommendationIntent.putExtra(RecommendedActivity.EXTRA_CITY_NAME, city)
                    startActivity(categoriesIntent)
                }
                R.id.liked -> {
                    val likedIntent = Intent()
                    likedIntent.setClass(this@GenreActivity, LikedMoviesActivity::class.java)
                    //recommendationIntent.putExtra(RecommendedActivity.EXTRA_CITY_NAME, city)
                    startActivity(likedIntent)
                }

            }
            true
        }
    }

    private fun initRecycleView() {
        binding.rvCategory.layoutManager = GridLayoutManager(this, 2)
        adapter = GenreAdapter(this)
        binding.rvCategory.adapter = adapter
    }

    override fun onGenreSelected(genre: Genre?) {
        val selectedGenreIntent = Intent()
        selectedGenreIntent.setClass(this@GenreActivity, SelectedGenreActivity::class.java)
        selectedGenreIntent.putExtra("GENRE_ID", genre?.id)
        startActivity(selectedGenreIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        return super.onCreateOptionsMenu(menu)
    }

}