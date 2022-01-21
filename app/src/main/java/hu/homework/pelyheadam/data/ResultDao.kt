package hu.homework.pelyheadam.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ResultDao {
    @Query("SELECT * FROM Liked_Movies")
    fun getAll() : List<Result>

    @Insert
    fun addLikedMovie(movie: Result?): Long

    @Delete
    fun removeLikedMovie(movie: Result?)

    @Query("SELECT EXISTS (SELECT 1 FROM Liked_Movies WHERE id = :id)")
    fun exists(id: Int?): Boolean
}