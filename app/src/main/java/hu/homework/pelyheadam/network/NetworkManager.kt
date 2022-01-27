package hu.homework.pelyheadam.network

import hu.homework.pelyheadam.entities.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkManager {
    private val retrofit: Retrofit
    private val movieApi: MovieApi

    private const val SERVICE_URL = "https://api.themoviedb.org"
    private const val API_KEY = "f2c2752c6d3fd659fdbc16c4be662490"

    init {
        retrofit = Retrofit.Builder()
            .baseUrl(SERVICE_URL)
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        movieApi = retrofit.create(MovieApi::class.java)
    }

    fun getPopular(page : Int): Call<MovieResult?>? {
        return movieApi.getPopular(API_KEY, "hu", page)
    }

    fun getRecommended(movie_id : Int?, page : Int) : Call<MovieResult?>? {
        return movieApi.getRecommended(movie_id, API_KEY, "hu", page)
    }

    fun getDetailsById(movie_id: Int) : Call<MovieDetails?>? {
        return movieApi.getDetailsById(movie_id, API_KEY, "hu", "credits")
    }

    fun getMoviesByGenre(genreId : Int, page : Int) : Call<MovieResult?>? {
        return movieApi.getMoviesByGenre(API_KEY, "hu", page, genreId, "vote_average.desc", 1000)
    }

    fun searchByTitle(queryString: String?) : Call<MovieResult?>? {
        return movieApi.searchByTitle(API_KEY, queryString, "hu",  "vote_average.desc")
    }

}
