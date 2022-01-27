package hu.homework.pelyheadam.activities

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.ContentValues.TAG
import hu.homework.pelyheadam.R
import hu.homework.pelyheadam.adapter.RecommendAdapter
import hu.homework.pelyheadam.data.MovieDatabase
import hu.homework.pelyheadam.databinding.ActivityRecommendedBinding
import hu.homework.pelyheadam.data.Result
import hu.homework.pelyheadam.databinding.RecommendedItemBinding
import hu.homework.pelyheadam.entities.MovieResult
import hu.homework.pelyheadam.interfaces.InitializeBottomMenu
import hu.homework.pelyheadam.interfaces.OnMovieClickListener
import hu.homework.pelyheadam.network.NetworkManager
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.concurrent.thread
import kotlin.random.Random

class RecommendedActivity : AppCompatActivity(), OnMovieClickListener, InitializeBottomMenu {

    private lateinit var binding: ActivityRecommendedBinding
    private lateinit var adapter: RecommendAdapter
    private lateinit var rowBinding: RecommendedItemBinding
    private lateinit var database: MovieDatabase
    private var page = 1
    private var totalPages = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecommendedBinding.inflate(layoutInflater)
        rowBinding = RecommendedItemBinding.inflate(layoutInflater)
        supportActionBar?.setTitle("Ajánlott")
        database = MovieDatabase.getDatabase(applicationContext)
        setContentView(binding.root)
        initBottomMenu()
        initRecyclerView()
    }

    // initialize click listener of the bottom menu
    override fun initBottomMenu() {
        binding.bottomNavigationView.menu.getItem(1).isChecked = true
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.popular -> {
                    val recommendationIntent = Intent()
                    recommendationIntent.setClass(this@RecommendedActivity, PopularActivity::class.java)
                    //recommendationIntent.putExtra(RecommendedActivity.EXTRA_CITY_NAME, city)
                    startActivity(recommendationIntent)
                }
                R.id.recommendation -> {
                    val recommendationIntent = Intent()
                    recommendationIntent.setClass(this@RecommendedActivity, RecommendedActivity::class.java)
                    //recommendationIntent.putExtra(RecommendedActivity.EXTRA_CITY_NAME, city)
                    startActivity(recommendationIntent)
                }
                R.id.categories -> {
                    val categoriesIntent = Intent()
                    categoriesIntent.setClass(this@RecommendedActivity, GenreActivity::class.java)
                    //recommendationIntent.putExtra(RecommendedActivity.EXTRA_CITY_NAME, city)
                    binding.bottomNavigationView.selectedItemId
                    startActivity(categoriesIntent)
                }
                R.id.liked -> {
                    val likedIntent = Intent()
                    likedIntent.setClass(this@RecommendedActivity, LikedMoviesActivity::class.java)
                    //recommendationIntent.putExtra(RecommendedActivity.EXTRA_CITY_NAME, city)
                    binding.bottomNavigationView.selectedItemId
                    startActivity(likedIntent)
                }

            }
            true
        }
    }

    private fun initRecyclerView() {
        binding.rvRecommend.layoutManager = LinearLayoutManager(this)
        adapter = RecommendAdapter(this, this)
        loadRecommendedMovies(0)
    }

    // call the get recommended movies function of api
    // the function requires a movie to get the recommended movies
    // in order to offer different movies this function always call the recommended api endpoint for different liked movies in the database
    private fun loadRecommendedMovies(position: Int) {
        thread {
            val likedMovies = database.ResultDao().getAll()
            runOnUiThread {
                if (!likedMovies.isEmpty()) {
                    var randomIndex = Random.nextInt(0,likedMovies.size)
                    getRecommendedMovies(likedMovies[randomIndex], position)    // get movies according to a random liked movie
                } else {
                    Toast.makeText(this@RecommendedActivity, "Nincsenek kedvelt filmek!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // call the recommended movies function of the api
    private fun getRecommendedMovies(r: Result, position: Int) {

        NetworkManager.getRecommended(r.id,page)?.enqueue(object : Callback<MovieResult?> {

            override fun onResponse(
                call: Call<MovieResult?>,
                response: Response<MovieResult?>
            ) {

                Log.d(ContentValues.TAG, "onResponse: " + response.code())
                if (response.isSuccessful) {
                    if (totalPages == -1) {
                        totalPages = response.body()?.total_pages!!
                    }
                    if (response.body()?.results?.size == 0) {
                        Toast.makeText(this@RecommendedActivity, "Nincs elég adatunk, hogy filmeket ajánljunk a(z) " + r.title + " alapján.", Toast.LENGTH_LONG).show()
                    }
                    displayRecommendedMovies(response.body(), position)
                } else {
                    Log.d(ContentValues.TAG, "Error: " + response.message())
                }
            }

            override fun onFailure(
                call: Call<MovieResult?>,
                t: Throwable
            ) {
                t.printStackTrace()
                Toast.makeText(this@RecommendedActivity, "Network request error occured, check LOG", Toast.LENGTH_LONG).show()
            }

        })

    }

    // display the movies
    private fun displayRecommendedMovies(body: MovieResult?, position: Int) {
        binding.cvProgress.visibility = View.GONE
        binding.rvRecommend.layoutManager?.scrollToPosition(position)

        for(movie : Result in body?.results!!) {
            adapter.addMovie(movie)
        }
        loadNewPageOnScroll()

        binding.rvRecommend.adapter = adapter
    }

    override fun onMovieSelected(movie: Result?) {
        val detailsIntent = Intent()
        detailsIntent.setClass(this@RecommendedActivity, MovieDetailsActivity::class.java)
        detailsIntent.putExtra("MOVIE_ID", movie?.id)
        detailsIntent.putExtra("SELECTED_ITEM", 1)
        startActivity(detailsIntent)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)

        val search = menu?.findItem(R.id.nav_search)
        val searchView = search?.actionView as SearchView
        searchView.queryHint = "Keresés"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                getSearchResults(query = newText)
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    private fun getSearchResults(query: String?) {
        NetworkManager.searchByTitle(query)?.enqueue(object: Callback<MovieResult?> {
            override fun onResponse(call: Call<MovieResult?>, response: Response<MovieResult?>) {
                Log.d(TAG, "onResponse: " + response.code())
                if (response.isSuccessful) {
                    displaySearchResults(response.body())
                } else {
                    Log.d(TAG, "Error: " + response.message())
                }
            }

            override fun onFailure(call: Call<MovieResult?>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(this@RecommendedActivity, "Hálózati hiba lépett fel! Ellenőrizd az internet kapcsolatod!", Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun displaySearchResults(body : MovieResult?) {
        binding.cvProgress.visibility = View.GONE
        adapter.removeAll()

        for(movie : Result in body?.results!!) {
            adapter.addMovie(movie)
        }
        loadNewPageOnScroll()

        binding.rvRecommend.adapter = adapter
    }


    // source: https://stackoverflow.com/questions/60316888/kotlin-scroll-detection-issue-in-recyclerview
    private fun loadNewPageOnScroll() {
        binding.rvRecommend.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    if (isLastVisible()) {
                        Log.e(ContentValues.TAG, "page: " + page + " total: " + totalPages)
                        if (page+1 <= totalPages) {
                            binding.cvProgress.visibility = View.VISIBLE
                            binding.rvRecommend.removeOnScrollListener(this)
                            page++
                            val layoutManager = binding.rvRecommend.layoutManager as LinearLayoutManager      // continue scrolling from the current position
                            val pos = layoutManager.findFirstVisibleItemPosition()
                            loadRecommendedMovies(pos)
                        }
                    }
                }
            }
        })
    }

    private fun isLastVisible(): Boolean {
        val layoutManager = binding.rvRecommend.layoutManager as LinearLayoutManager
        val pos = layoutManager.findLastCompletelyVisibleItemPosition()
        val itemsCount = adapter.itemCount
        return (pos >= itemsCount - 1)
    }

}