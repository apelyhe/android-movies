package hu.homework.pelyheadam.data

import android.content.Context
import androidx.room.*

@Database(entities = [Result::class], version = 1)
@TypeConverters(Converters::class)
abstract class MovieDatabase : RoomDatabase(){
    abstract fun ResultDao(): ResultDao

    companion object {
        fun getDatabase(applicationContext: Context): MovieDatabase {
            return Room.databaseBuilder(
                applicationContext,
                MovieDatabase::class.java,
                "watchIT"
            ).build();
        }
    }

}