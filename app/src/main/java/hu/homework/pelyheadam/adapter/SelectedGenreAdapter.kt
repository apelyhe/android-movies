package hu.homework.pelyheadam.adapter

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.like.LikeButton
import com.like.OnLikeListener
import com.squareup.picasso.Picasso
import hu.homework.pelyheadam.R
import hu.homework.pelyheadam.data.MovieDatabase
import hu.homework.pelyheadam.databinding.SelectedGenreItemBinding
import hu.homework.pelyheadam.data.Result
import hu.homework.pelyheadam.interfaces.OnMovieClickListener
import kotlin.concurrent.thread

class SelectedGenreAdapter(context: Context, private val listener: OnMovieClickListener)
    : RecyclerView.Adapter<SelectedGenreAdapter.SelectedGenreViewHolder>(){

    private val movies = mutableListOf<Result?>()
    private var database: MovieDatabase = MovieDatabase.getDatabase(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedGenreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.selected_genre_item, parent, false)
        return SelectedGenreViewHolder(view)
    }

    override fun onBindViewHolder(holder: SelectedGenreViewHolder, position: Int) {
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
        holder.binding.tvRating.text = "Értékelés: " + movieItem.vote_average.toString()
    }

    override fun getItemCount(): Int = movies.size

    inner class SelectedGenreViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = SelectedGenreItemBinding.bind(itemView)
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
            //binding.CityItemNameTextView.text = item
            binding.tvTitle.text = item?.title ?: ""
            binding.tvRating.text = item?.popularity.toString() ?: ""
        }
    }


    fun addMovie(movie: Result?) {
        movies.add(movie)
        notifyItemInserted(movies.size - 1)
    }

    fun removeAll() {
        movies.clear()
        notifyItemRemoved(movies.size - 1)
    }
}