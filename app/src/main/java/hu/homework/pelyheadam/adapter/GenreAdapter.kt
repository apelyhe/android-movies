package hu.homework.pelyheadam.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.homework.pelyheadam.R
import hu.homework.pelyheadam.databinding.GenreItemBinding
import hu.homework.pelyheadam.entities.Genre
import hu.homework.pelyheadam.lists.GenreTypes
import hu.homework.pelyheadam.interfaces.OnGenreSelectedListener


class GenreAdapter(private val listener: OnGenreSelectedListener)
    : RecyclerView.Adapter<GenreAdapter.GenreViewHolder>() {

    private var genreTypes = GenreTypes()
    private var genres = genreTypes.getAllType()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder{
        val view = LayoutInflater.from(parent.context).inflate(R.layout.genre_item, parent, false)
        return GenreViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        val genreItem = genres[position]
        holder.item = genreItem
        holder.binding.tvGenre.text = genreItem.name
    }

    override fun getItemCount(): Int = genres.size

    inner class GenreViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = GenreItemBinding.bind(itemView)
        var item: Genre? = null

        init {
            binding.root.setOnClickListener { listener.onGenreSelected(item) }
        }

        fun bind(genre: Genre?) {
            item = genre
            binding.tvGenre.text = item?.name ?: ""
        }

    }


}