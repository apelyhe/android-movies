package hu.homework.pelyheadam.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// kotlin data class generated from json file
// source: https://plugins.jetbrains.com/plugin/9960-json-to-kotlin-class-jsontokotlinclass-

@Entity(tableName = "Liked_Movies")
data class Result(
    @ColumnInfo(name = "adult") val adult: Boolean?,
    @ColumnInfo(name = "backdrop_path") val backdrop_path: String?,
    @ColumnInfo(name = "genre_ids") val genre_ids: List<Int>?,
    @ColumnInfo(name = "id") @PrimaryKey val id: Int,
    @ColumnInfo(name = "original_language") val original_language: String?,
    @ColumnInfo(name = "original_title") val original_title: String?,
    @ColumnInfo(name = "overview") val overview: String?,
    @ColumnInfo(name = "popularity") val popularity: Double?,
    @ColumnInfo(name = "poster_path") val poster_path: String?,
    @ColumnInfo(name = "release_date") val release_date: String?,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "video") val video: Boolean?,
    @ColumnInfo(name = "vote_average") val vote_average: Double?,
    @ColumnInfo(name = "vote_count") val vote_count: Int?
)