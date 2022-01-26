package hu.homework.pelyheadam.activities

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import com.squareup.picasso.Picasso
import hu.homework.pelyheadam.R
import hu.homework.pelyheadam.databinding.ActivityMovieDetailsBinding
import hu.homework.pelyheadam.entities.MovieDetails
import hu.homework.pelyheadam.interfaces.InitializeBottomMenu
import hu.homework.pelyheadam.network.NetworkManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieDetailsActivity : AppCompatActivity(), InitializeBottomMenu {

    private lateinit var binding : ActivityMovieDetailsBinding
    private var movieId : Int = 0
    private var selectedItemId : Int = 0

    companion object {
        var MOVIE_ID = "0"
        var SELECTED_ITEM = "0"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setTitle("Adatok")
        movieId = intent.getIntExtra("MOVIE_ID", 0)
        selectedItemId = intent.getIntExtra("SELECTED_ITEM", 0)
        initBottomMenu()

        loadScreen()
    }

    private fun loadScreen() {
        NetworkManager.getDetailsById(movieId)?.enqueue(object : Callback<MovieDetails?> {
            override fun onResponse(
                call: Call<MovieDetails?>,
                response: Response<MovieDetails?>
            ) {
                Log.d(ContentValues.TAG, "onResponse: " + response.code())
                if (response.isSuccessful) {
                    displayDetails(response.body())
                } else {
                    Log.d(ContentValues.TAG, "Error: " + response.message())
                }
            }

            override fun onFailure(
                call: Call<MovieDetails?>,
                t: Throwable
            ) {
                t.printStackTrace()
                Toast.makeText(this@MovieDetailsActivity, "Hálózati hiba lépett fel! Ellenőrizd az internet kapcsolatod!", Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun displayDetails(body: MovieDetails?) {
        val posterImageUri = Uri.parse("https://image.tmdb.org/t/p/original/" + body!!.poster_path)
        val backdropImageUri = Uri.parse("https://image.tmdb.org/t/p/original/" + body.backdrop_path)
        Picasso.get().load(backdropImageUri).into(binding.ivBackdrop)
        Picasso.get().load(posterImageUri).into(binding.ivPoster)
        binding.tvRating.text = body.vote_average.toString() + " / 10"

        var date = body.release_date.split("-")
        binding.tvReleaseDate.text = "Megjelenés: " + date[0] + "." + date[1] + "." + date[2] + "."
        binding.tvOverview.text = body.overview
        binding.tvOriginalTitle.text = body.original_title
        binding.tvTitle.text = body.title
    }

    override fun initBottomMenu() {
        binding.bottomNavigationView.menu.getItem(selectedItemId).isChecked = true
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.popular -> {
                    val recommendationIntent = Intent()
                    recommendationIntent.setClass(this@MovieDetailsActivity, PopularActivity::class.java)
                    //recommendationIntent.putExtra(RecommendedActivity.EXTRA_CITY_NAME, city)
                    startActivity(recommendationIntent)
                }
                R.id.recommendation -> {
                    val categoriesIntent = Intent()
                    categoriesIntent.setClass(this@MovieDetailsActivity, RecommendedActivity::class.java)
                    //recommendationIntent.putExtra(RecommendedActivity.EXTRA_CITY_NAME, city)
                    startActivity(categoriesIntent)
                }
                R.id.categories -> {
                    val categoriesIntent = Intent()
                    categoriesIntent.setClass(this@MovieDetailsActivity, GenreActivity::class.java)
                    //recommendationIntent.putExtra(RecommendedActivity.EXTRA_CITY_NAME, city)
                    startActivity(categoriesIntent)
                }
                R.id.liked -> {
                    val likedIntent = Intent()
                    likedIntent.setClass(this@MovieDetailsActivity, LikedMoviesActivity::class.java)
                    //recommendationIntent.putExtra(RecommendedActivity.EXTRA_CITY_NAME, city)
                    binding.bottomNavigationView.selectedItemId
                    startActivity(likedIntent)
                }

            }
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        return super.onCreateOptionsMenu(menu)
    }

}