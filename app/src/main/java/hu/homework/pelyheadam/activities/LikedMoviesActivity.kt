package hu.homework.pelyheadam.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import hu.homework.pelyheadam.R
import hu.homework.pelyheadam.adapter.LikedMoviesAdapter
import hu.homework.pelyheadam.adapter.RecommendAdapter
import hu.homework.pelyheadam.data.MovieDatabase
import hu.homework.pelyheadam.data.Result
import hu.homework.pelyheadam.databinding.ActivityLikedMoviesBinding
import hu.homework.pelyheadam.databinding.ActivityRecommendedBinding
import hu.homework.pelyheadam.interfaces.OnMovieClickListener
import kotlin.concurrent.thread

class LikedMoviesActivity : AppCompatActivity(), OnMovieClickListener {

    private lateinit var binding: ActivityLikedMoviesBinding
    private lateinit var adapter: LikedMoviesAdapter
    private lateinit var database: MovieDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLikedMoviesBinding.inflate(layoutInflater)
        supportActionBar?.setTitle("Kedvelt")
        setContentView(R.layout.activity_liked_movies)
        database = MovieDatabase.getDatabase(applicationContext)
        setContentView(binding.root)
        initBottomMenu()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.rvLiked.layoutManager = LinearLayoutManager(this)
        adapter = LikedMoviesAdapter(this, this)
        loadLikedMovies()
    }

    private fun loadLikedMovies() {
        thread {
            val movies = database.ResultDao().getAll()
            runOnUiThread {
                for (movie: Result in movies) {
                    adapter.addMovie(movie)
                }
                binding.rvLiked.adapter = adapter
            }
        }

    }

    private fun initBottomMenu() {
        binding.bottomNavigationView.menu.getItem(3).isChecked = true
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.popular -> {
                    val recommendationIntent = Intent()
                    recommendationIntent.setClass(this@LikedMoviesActivity, PopularActivity::class.java)
                    //recommendationIntent.putExtra(RecommendedActivity.EXTRA_CITY_NAME, city)
                    startActivity(recommendationIntent)
                }
                R.id.recommendation -> {
                    val recommendationIntent = Intent()
                    recommendationIntent.setClass(this@LikedMoviesActivity, RecommendedActivity::class.java)
                    //recommendationIntent.putExtra(RecommendedActivity.EXTRA_CITY_NAME, city)
                    startActivity(recommendationIntent)
                }
                R.id.categories -> {
                    val categoriesIntent = Intent()
                    categoriesIntent.setClass(this@LikedMoviesActivity, GenreActivity::class.java)
                    //recommendationIntent.putExtra(RecommendedActivity.EXTRA_CITY_NAME, city)
                    binding.bottomNavigationView.selectedItemId
                    startActivity(categoriesIntent)
                }
                R.id.liked -> {
                    val likedIntent = Intent()
                    likedIntent.setClass(this@LikedMoviesActivity, LikedMoviesActivity::class.java)
                    //recommendationIntent.putExtra(RecommendedActivity.EXTRA_CITY_NAME, city)
                    binding.bottomNavigationView.selectedItemId
                    startActivity(likedIntent)
                }

            }
            true
        }
    }

    override fun onMovieSelected(movie: Result?) {
        val detailsIntent = Intent()
        detailsIntent.setClass(this@LikedMoviesActivity, MovieDetailsActivity::class.java)
        detailsIntent.putExtra("MOVIE_ID", movie?.id)
        detailsIntent.putExtra("SELECTED_ITEM", 3)
        startActivity(detailsIntent)
    }
}