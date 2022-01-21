package hu.homework.pelyheadam.network

import hu.homework.pelyheadam.entities.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {

    // https://api.themoviedb.org/3/movie/popular?api_key=<<api_key>>&language=en-US&page=1
    @GET("/3/movie/popular")
    fun getPopular(
        @Query("api_key") api_key: String,
        @Query("language") language: String?,
        @Query("page") page: Int?
    ): Call<MovieResult?>?


    // https://api.themoviedb.org/3/movie/{movie_id}/recommendations?api_key=<<api_key>>&language=en-US&page=1
    @GET("/3/movie/{movie_id}/recommendations")
    fun getRecommended(
        @Path("movie_id") movie_id : Int?,
        @Query("api_key") api_key: String,
        @Query("language") language: String?,
        @Query("page") page: Int?
    ): Call<MovieResult?>?


    //https://api.themoviedb.org/3/movie/{movie_id}?api_key=<<api_key>>&language=en-US
    @GET("/3/movie/{movie_id}")
    fun getDetailsById(
        @Path("movie_id") movie_id: Int,
        @Query("api_key") api_key: String,
        @Query("language") language: String?
    ) : Call<MovieDetails?>?

    //https://api.themoviedb.org/3/discover/movie?api_key=XXXXX&with_genres=27
    @GET("/3/discover/movie")
    fun getMoviesByGenre(
        @Query("api_key") api_key: String,
        @Query("language") language: String?,
        @Query("page") page: Int?,
        @Query("with_genres") genreId: Int,
        @Query("sort_by") sort_by: String,
        @Query("vote_count.gte") vote_count_min: Int
    ) : Call<MovieResult?>?

    //https://api.themoviedb.org/3/search/movie?api_key=###&query=the+avengers
    @GET("/3/search/movie")
    fun searchByTitle(
        @Query("api_key") api_key: String,
        @Query("query") queryString: String?,
        @Query("language") language: String?,
        @Query("sort_by") sort_by: String,
        @Query("vote_count.gte") vote_count_min: Int
    ) : Call<MovieResult?>?

}