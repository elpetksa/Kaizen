package com.elpet.kaizen.ui.fragment.home.models

import com.elpet.kaizen.data.model.local.Sport

data class HomeSport(
    /**
     * The corresponding sport.
     */
    val sport: Sport,

    /**
     * Defines if sport holder is expanded.
     */
    var isExpanded: Boolean = false
)