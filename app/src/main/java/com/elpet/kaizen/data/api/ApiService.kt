package com.elpet.kaizen.data.api

import com.elpet.kaizen.data.model.response.ResponseGetSports
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    /**
     * Retrieve the list of all available sports with their events.
     *
     * @return [Response] that if successful contains a [ResponseGetSports] object.
     */
    @GET("/sports")
    suspend fun getSports(): Response<List<ResponseGetSports>>
}