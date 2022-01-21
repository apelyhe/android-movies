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
import hu.homework.pelyheadam.R
import hu.homework.pelyheadam.adapter.SelectedGenreAdapter
import hu.homework.pelyheadam.databinding.ActivitySelectedGenreBinding
import hu.homework.pelyheadam.entities.MovieResult
import hu.homework.pelyheadam.data.Result
import hu.homework.pelyheadam.interfaces.OnMovieClickListener
import hu.homework.pelyheadam.lists.GenreTypes
import hu.homework.pelyheadam.network.NetworkManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectedGenreActivity : AppCompatActivity(), OnMovieClickListener {

    private lateinit var binding: ActivitySelectedGenreBinding
    private lateinit var adapter: SelectedGenreAdapter
    private var genreId: Int = 0
    private var movies: MovieResult? = null
    private var page = 1

    companion object {
        var GENRE_ID = "0"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectedGenreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        genreId = intent.getIntExtra("GENRE_ID", 0)

        val types = GenreTypes()

        supportActionBar?.setTitle(types.getGenreById(genreId))
        initBottomMenu()
        initRecyclerView()
    }

    private fun initBottomMenu() {
        binding.bottomNavigationView.menu.getItem(2).isChecked = true
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.popular -> {
                    val recommendationIntent = Intent()
                    recommendationIntent.setClass(this@SelectedGenreActivity, PopularActivity::class.java)
                    //recommendationIntent.putExtra(RecommendedActivity.EXTRA_CITY_NAME, city)
                    startActivity(recommendationIntent)
                }
                R.id.recommendation -> {
                    val recommendationIntent = Intent()
                    recommendationIntent.setClass(this@SelectedGenreActivity, RecommendedActivity::class.java)
                    //recommendationIntent.putExtra(RecommendedActivity.EXTRA_CITY_NAME, city)
                    startActivity(recommendationIntent)
                }
                R.id.categories -> {
                    val categoriesIntent = Intent()
                    categoriesIntent.setClass(this@SelectedGenreActivity, GenreActivity::class.java)
                    //recommendationIntent.putExtra(RecommendedActivity.EXTRA_CITY_NAME, city)
                    binding.bottomNavigationView.selectedItemId
                    startActivity(categoriesIntent)
                }
                R.id.liked -> {
                    val likedIntent = Intent()
                    likedIntent.setClass(this@SelectedGenreActivity, LikedMoviesActivity::class.java)
                    //recommendationIntent.putExtra(RecommendedActivity.EXTRA_CITY_NAME, city)
                    binding.bottomNavigationView.selectedItemId
                    startActivity(likedIntent)
                }

            }
            true
        }
    }

    private fun initRecyclerView() {
        binding.rvSelectedGenre.layoutManager = LinearLayoutManager(this)
        adapter = SelectedGenreAdapter(this, this)
        loadGenreMovies(0)
    }


    override fun onMovieSelected(movie: Result?) {
        val detailsIntent = Intent()
        detailsIntent.setClass(this@SelectedGenreActivity, MovieDetailsActivity::class.java)
        detailsIntent.putExtra("MOVIE_ID", movie?.id)
        detailsIntent.putExtra("SELECTED_ITEM", 2)
        startActivity(detailsIntent)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)

        val search = menu?.findItem(R.id.nav_search)
        val searchView = search?.actionView as SearchView
        searchView.queryHint = "Keresés"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                //showResults(query)
                return true
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
                Log.d(ContentValues.TAG, "onResponse: " + response.code())
                if (response.isSuccessful) {
                    displaySearchResults(response.body())
                } else {
                    Log.d(ContentValues.TAG, "Error: " + response.message())
                }
            }

            override fun onFailure(call: Call<MovieResult?>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(this@SelectedGenreActivity, "Network request error occured, check LOG", Toast.LENGTH_LONG).show()
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

        binding.rvSelectedGenre.adapter = adapter
    }


    private fun loadGenreMovies(position: Int) {
        NetworkManager.getMoviesByGenre(genreId,page)?.enqueue(object : Callback<MovieResult?> {
            override fun onResponse(
                call: Call<MovieResult?>,
                response: Response<MovieResult?>
            ) {
                Log.d(ContentValues.TAG, "onResponse: " + response.code())
                if (response.isSuccessful) {
                    displayMovies(response.body(), position)
                } else {
                    Log.d(ContentValues.TAG, "Error: " + response.message())
                }
            }

            override fun onFailure(
                call: Call<MovieResult?>,
                t: Throwable
            ) {
                t.printStackTrace()
                Toast.makeText(this@SelectedGenreActivity, "Network request error occured, check LOG", Toast.LENGTH_LONG).show()
            }


        })
    }

    private fun displayMovies(body: MovieResult?, position: Int) {
        binding.cvProgress.visibility = View.GONE
        binding.rvSelectedGenre.layoutManager?.scrollToPosition(position)
        movies = body
        for(movie : Result in movies?.results!!) {
            adapter.addMovie(movie)
        }
        loadNewPageOnScroll()

        binding.rvSelectedGenre.adapter = adapter

    }


    // source: https://stackoverflow.com/questions/60316888/kotlin-scroll-detection-issue-in-recyclerview
    private fun loadNewPageOnScroll() {
        binding.rvSelectedGenre.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    if (isLastVisible()) {
                        binding.cvProgress.visibility = View.VISIBLE
                        binding.rvSelectedGenre.removeOnScrollListener(this)
                        page++
                        val layoutManager = binding.rvSelectedGenre.layoutManager as LinearLayoutManager      // continue scrolling from the current position
                        val pos = layoutManager.findFirstVisibleItemPosition()
                        loadGenreMovies(pos)
                    }
                }
            }
        })
    }

    private fun isLastVisible(): Boolean {
        val layoutManager = binding.rvSelectedGenre.layoutManager as LinearLayoutManager
        val pos = layoutManager.findLastCompletelyVisibleItemPosition()
        val itemsCount = adapter.itemCount
        return (pos >= itemsCount - 1)
    }
}