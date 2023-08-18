package com.elpet.kaizen.data.database

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase
import com.elpet.kaizen.data.model.local.Event

@Dao
interface EventDao {
    @Query("SELECT * FROM event")
    fun getAll(): List<Event>

    @Delete
    fun delete(event: Event)

    @Insert
    fun insertAll(vararg events: Event)
}

@Database(entities = [Event::class], version = 1, exportSchema = false)
abstract class FavoriteEventsDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
}