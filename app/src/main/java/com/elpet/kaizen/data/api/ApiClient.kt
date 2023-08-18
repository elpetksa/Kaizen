package com.elpet.kaizen.data.api

import com.elpet.kaizen.data.model.response.ResponseGetSports
import retrofit2.Response
import javax.inject.Inject

class ApiClient @Inject constructor(
    private val apiService: ApiService
) {
    /**
     * Retrieve the list of all available sports with their events.
     *
     * @return [Response] that if successful contains a [ResponseGetSports] object.
     */
    suspend fun getSports(): Response<List<ResponseGetSports>> =
        apiService.getSports()
}