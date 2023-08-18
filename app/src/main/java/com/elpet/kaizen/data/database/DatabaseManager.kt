package com.elpet.kaizen.data.database

import com.elpet.kaizen.data.model.local.Event
import javax.inject.Inject

interface DatabaseManager {
    fun getFavoriteEvents(): List<Event>
    fun addFavoriteEvent(event: Event)
    fun deleteFavoriteEvent(event: Event)
}

class DatabaseManagerImpl @Inject constructor(
    private val favoriteEventsDatabase: FavoriteEventsDatabase
): DatabaseManager {
    override fun getFavoriteEvents(): List<Event> {
        return favoriteEventsDatabase.eventDao().getAll()
    }

    override fun addFavoriteEvent(event: Event) {
        favoriteEventsDatabase.eventDao().insertAll(event)
    }

    override fun deleteFavoriteEvent(event: Event) {
        favoriteEventsDatabase.eventDao().delete(event)
    }
}