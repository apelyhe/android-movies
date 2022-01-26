package hu.homework.pelyheadam.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.like.LikeButton
import com.like.OnLikeListener
import com.squareup.picasso.Picasso
import hu.homework.pelyheadam.R
import hu.homework.pelyheadam.data.MovieDatabase
import hu.homework.pelyheadam.data.Result
import hu.homework.pelyheadam.databinding.PopularItemBinding
import hu.homework.pelyheadam.interfaces.OnMovieClickListener
import kotlin.concurrent.thread

class LikedMoviesAdapter(context: Context, private val listener: OnMovieClickListener)
    : RecyclerView.Adapter<LikedMoviesAdapter.LikedMoviesViewHolder>() {

    private val movies = mutableListOf<Result?>()
    private var database: MovieDatabase = MovieDatabase.getDatabase(context)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LikedMoviesAdapter.LikedMoviesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.liked_movie_item, parent, false)
        return LikedMoviesViewHolder(view)
    }

    override fun onBindViewHolder(holder: LikedMoviesAdapter.LikedMoviesViewHolder, position: Int) {
        val movieItem = movies[position]

        holder.item = movieItem
        thread {
            if (database.ResultDao().exists(holder.item?.id)) {
                holder.binding.btnLike.isLiked = true
            }
        }
        val imageUri = Uri.parse("https://image.tmdb.org/t/p/original/" + movieItem!!.poster_path)
        Picasso.get().load(imageUri).into(holder.binding.ivPoster)
        holder.binding.tvTitle.text = movieItem.title
        holder.binding.tvPopularity.text = movieItem.popularity?.toInt().toString() + " szavazat"
    }

    override fun getItemCount(): Int = movies.size

    inner class LikedMoviesViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = PopularItemBinding.bind(itemView)
        var item: Result? = null

        init {
            binding.btnLike.setOnLikeListener(object: OnLikeListener {
                override fun liked(likeButton: LikeButton?) {
                    addMovieToDatabaseInBackground()
                }

                override fun unLiked(likeButton: LikeButton?) {
                    removeMovieFromDatabaseInBackground()
                }

            })
            binding.root.setOnClickListener { listener.onMovieSelected(item) }
        }

        private fun addMovieToDatabaseInBackground() {
            thread {
                database.ResultDao().addLikedMovie(item)
            }
        }

        private fun removeMovieFromDatabaseInBackground() {
            thread {
                database.ResultDao().removeLikedMovie(item)
            }
        }

        fun bind(newMovie: Result?) {
            item = newMovie
            binding.tvTitle.text = item?.title ?: ""
            binding.tvPopularity.text = item?.popularity.toString() ?: ""
        }
    }

    fun addMovie(movie: Result?) {
        movies.add(movie)
        notifyItemInserted(movies.size - 1)
    }
}