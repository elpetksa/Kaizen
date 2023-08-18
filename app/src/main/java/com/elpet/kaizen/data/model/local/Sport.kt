package com.elpet.kaizen.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Sport (
    /**
     * Sport id.
     */
    @PrimaryKey
    val id: String,

    /**
     * Sport name.
     */
    val name: String,

    /**
     * Sport events.
     */
    val events: List<Event>
)