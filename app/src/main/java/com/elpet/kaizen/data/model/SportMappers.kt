package com.elpet.kaizen.data.model

import com.elpet.kaizen.data.model.local.Event
import com.elpet.kaizen.data.model.local.Sport
import com.elpet.kaizen.data.model.response.ResponseGetSports
import com.elpet.kaizen.ui.fragment.home.models.HomeSport
import kotlin.random.Random

fun List<Sport>.toHomeSports(): List<HomeSport> {
    return map { sport ->
        HomeSport(
            sport = sport
        )
    }
}

fun List<ResponseGetSports>.toSports(favoriteEventsIds: List<String>): List<Sport> {
    return map { sport ->
        Sport(
            id = sport.id,
            name = sport.name,
            events = sport.events.sortedBy { event -> event.startTime }.map { event ->
                Event(
                    id = event.id,
                    sportId = event.sportId,
                    name = event.name,
                    startTime = System.currentTimeMillis() / 1000 + Random.nextInt(100000),//event.startTime
                    isFavorite = favoriteEventsIds.contains(event.id)
                )
            }
        )
    }
}