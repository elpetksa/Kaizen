package com.elpet.kaizen.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Event (
    /**
     * Event id.
     */
    @PrimaryKey
    val id: String,

    /**
     * Id of sport event belongs to.
     */
    val sportId: String,

    /**
     * Name of event.
     */
    val name: String,

    /**
     * Timestamp (seconds) event starts.
     */
    val startTime: Long,

    /**
     * Tournament name of event. Just some random data to fill UI :)
     */
    val tournament: String = listOf(
        "League A",
        "League B",
        "Super league",
        "UEFA",
        ""
    ).random(),

    /**
     * Defines if this event is set as favorite.
     */
    var isFavorite: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (other !is Event) return false
        return other.id == id
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + sportId.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + startTime.hashCode()
        return result
    }
}