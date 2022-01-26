package hu.homework.pelyheadam.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.SearchView
import android.widget.Toast
import hu.homework.pelyheadam.adapter.PopularAdapter
import hu.homework.pelyheadam.databinding.*
import hu.homework.pelyheadam.interfaces.OnMovieClickListener
import hu.homework.pelyheadam.network.NetworkManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.ContentValues.TAG
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.homework.pelyheadam.R
import hu.homework.pelyheadam.entities.MovieResult
import hu.homework.pelyheadam.data.Result
import hu.homework.pelyheadam.interfaces.InitializeBottomMenu

class PopularActivity() : AppCompatActivity(), OnMovieClickListener, InitializeBottomMenu {

    private lateinit var binding : ActivityPopularBinding
    private lateinit var adapter : PopularAdapter
    private lateinit var rowBinding: PopularItemBinding
    private var popularMovies : MovieResult? = null
    private var page = 1
    private var totalPages = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPopularBinding.inflate(layoutInflater)
        setContentView(binding.root)
        rowBinding = PopularItemBinding.inflate(layoutInflater)
        supportActionBar?.setTitle("Felkapott")
        initRecyclerView()
        initBottomMenu()
    }

    // initialize click listener of the bottom menu
    override fun initBottomMenu() {
        binding.bottomNavigationView.menu.getItem(0).isChecked = true
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.popular -> {
                    val recommendationIntent = Intent()
                    recommendationIntent.setClass(this@PopularActivity, PopularActivity::class.java)
                    //recommendationIntent.putExtra(RecommendedActivity.EXTRA_CITY_NAME, city)
                    startActivity(recommendationIntent)
                }
                R.id.recommendation -> {
                    val recommendationIntent = Intent()
                    recommendationIntent.setClass(this@PopularActivity, RecommendedActivity::class.java)
                    //recommendationIntent.putExtra(RecommendedActivity.EXTRA_CITY_NAME, city)
                    startActivity(recommendationIntent)
                }
                R.id.categories -> {
                    val categoriesIntent = Intent()
                    categoriesIntent.setClass(this@PopularActivity, GenreActivity::class.java)
                    //recommendationIntent.putExtra(RecommendedActivity.EXTRA_CITY_NAME, city)
                    binding.bottomNavigationView.selectedItemId
                    startActivity(categoriesIntent)
                }
                R.id.liked -> {
                    val likedIntent = Intent()
                    likedIntent.setClass(this@PopularActivity, LikedMoviesActivity::class.java)
                    //recommendationIntent.putExtra(RecommendedActivity.EXTRA_CITY_NAME, city)
                    binding.bottomNavigationView.selectedItemId
                    startActivity(likedIntent)
                }

            }
            true
        }
    }

    private fun initRecyclerView() {
        binding.rvPopular.layoutManager = LinearLayoutManager(this)
        adapter = PopularAdapter(this, this)
        loadPopularMovies(0)
    }

    override fun onMovieSelected(movie: Result?) {
        val detailsIntent = Intent()
        detailsIntent.setClass(this@PopularActivity, MovieDetailsActivity::class.java)
        detailsIntent.putExtra("MOVIE_ID", movie?.id)
        detailsIntent.putExtra("SELECTED_ITEM", 0)
        startActivity(detailsIntent)
    }

    // for the search bar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)

        val search = menu?.findItem(R.id.nav_search)
        val searchView = search?.actionView as SearchView
        searchView.queryHint = "Keresés"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                //showResults(query)
                return true
            }
            // if the text changes, it refreshes the search results
            override fun onQueryTextChange(newText: String?): Boolean {
                getSearchResults(query = newText)
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    // call the search function from the API and display the search results if it was successful
    private fun getSearchResults(query: String?) {
        NetworkManager.searchByTitle(query)?.enqueue(object: Callback<MovieResult?> {
            override fun onResponse(call: Call<MovieResult?>, response: Response<MovieResult?>) {
                Log.d(TAG, "onResponse: " + response.code())
                if (response.isSuccessful) {
                    if (totalPages == -1) {
                        totalPages = response.body()?.total_pages!!
                    }
                    displaySearchResults(response.body())
                } else {
                    Log.d(TAG, "Error: " + response.message())
                }
            }

            override fun onFailure(call: Call<MovieResult?>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(this@PopularActivity, "Hálózati hiba lépett fel! Ellenőrizd az internet kapcsolatod!", Toast.LENGTH_LONG).show()
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

        binding.rvPopular.adapter = adapter
    }

    // call the get popular movies function of api
    private fun loadPopularMovies(position: Int) {

        NetworkManager.getPopular(page)?.enqueue(object : Callback<MovieResult?> {

            override fun onResponse(
                call: Call<MovieResult?>,
                response: Response<MovieResult?>
            ) {
                Log.d(TAG, "onResponse: " + response.code())
                if (response.isSuccessful) {
                    displayPopularMovies(response.body(), position)
                } else {
                    Log.d(TAG, "Error: " + response.message())
                }
            }

            override fun onFailure(
                call: Call<MovieResult?>,
                t: Throwable
            ) {
                t.printStackTrace()
                Toast.makeText(this@PopularActivity, "Network request error occured, check LOG", Toast.LENGTH_LONG).show()
            }

        })
    }

    // display the popular movies in the recyclerview
    private fun displayPopularMovies(body: MovieResult?, position: Int) {
        binding.cvProgress.visibility = View.GONE
        binding.rvPopular.layoutManager?.scrollToPosition(position)

        popularMovies = body
        for(movie : Result in popularMovies?.results!!) {
            adapter.addMovie(movie)
        }
        loadNewPageOnScroll()

        binding.rvPopular.adapter = adapter

    }


    // source: https://stackoverflow.com/questions/60316888/kotlin-scroll-detection-issue-in-recyclerview
    private fun loadNewPageOnScroll() {
        binding.rvPopular.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // if the direction of the scroll is downwards
                if (dy > 0) {
                    // if the last item is visible, show the progress bar, and request the next page
                    if (isLastVisible()) {
                        if (page + 1 <= totalPages) {
                            binding.cvProgress.visibility = View.VISIBLE
                            binding.rvPopular.removeOnScrollListener(this)
                            page++
                            val layoutManager = binding.rvPopular.layoutManager as LinearLayoutManager
                            val pos = layoutManager.findFirstVisibleItemPosition()
                            loadPopularMovies(pos)
                        }
                    }
                }
            }

        })
    }

    // returns if the last loaded item is visible or not
    private fun isLastVisible(): Boolean {
        val layoutManager = binding.rvPopular.layoutManager as LinearLayoutManager
        val pos = layoutManager.findLastCompletelyVisibleItemPosition()
        val itemsCount = adapter.itemCount
        return (pos >= itemsCount - 1)
    }

}
