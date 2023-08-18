package com.elpet.kaizen.data.model.response

import com.elpet.kaizen.data.model.local.Event
import com.google.gson.annotations.SerializedName
import com.elpet.kaizen.data.model.local.Sport

data class ResponseGetSports (
    @SerializedName("i") val id: String,
    @SerializedName("d") val name : String,
    @SerializedName("e") val events : List<ResponseGetSportsEvent>,
)

data class ResponseGetSportsEvent (
    @SerializedName("i") val id: String,
    @SerializedName("si") val sportId: String,
    @SerializedName("d") val name: String,
    @SerializedName("tt") val startTime: Long,
)