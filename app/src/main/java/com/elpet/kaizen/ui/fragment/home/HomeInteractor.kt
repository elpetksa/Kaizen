package com.elpet.kaizen.ui.fragment.home

import android.content.Context
import com.elpet.kaizen.R
import com.elpet.kaizen.core.AppLogger
import com.elpet.kaizen.data.api.ApiClient
import com.elpet.kaizen.data.database.FavoriteEventsDatabase
import com.elpet.kaizen.data.model.local.Event
import com.elpet.kaizen.data.model.local.Sport
import com.elpet.kaizen.data.model.toSports
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface HomeInteractor {
    /**
     * Retrieve the list of all available sports with their events.
     *
     * @return A list of [Sport] objects with their corresponding list of [Event].
     */
    fun getSports(): Flow<GetSportsPartialState>
}

class HomeInteractorImpl @Inject constructor(
    @ApplicationContext val context: Context,
    private val apiClient: ApiClient,
    private val logger: AppLogger,
    private val favoriteEventsDatabase: FavoriteEventsDatabase
) : HomeInteractor {

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ FUNCTIONS                                                                                 ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    // ┌───────────────────────────────────────────────────────────────────────────────────────────┐
    //   → OVERRIDE FUNCTIONS
    // └───────────────────────────────────────────────────────────────────────────────────────────┘

    /**
     * Retrieve the list of all available sports with their events.
     *
     * @return A list of [Sport] objects with their corresponding list of [Event].
     */
    override fun getSports(): Flow<GetSportsPartialState> = flow {
        logger.d("[${javaClass.name}] :: Requesting sports list from server.")

        // Initialize generic error message.
        val errorMessage = context.getString(R.string.generic_error_message)

        try {
            apiClient.getSports().let { response ->
                response.body()?.let {
                    // Get favorite events.
                    val favoriteEvents = favoriteEventsDatabase.eventDao().getAll()

                    // Convert response to sports.
                    val sports = it.toSports(favoriteEvents.map { event -> event.id })

                    emit(GetSportsPartialState.Success(sports))
                } ?: emit(GetSportsPartialState.Failed(errorMessage))
            }

        } catch (exception: Exception) {
            logger.e("[${javaClass.name}] :: Failed to get sports list.", exception)
            emit(GetSportsPartialState.Failed(errorMessage))
        }
    }
}

// ╔═══════════════════════════════════════════════════════════════════════════════════════════════╗
// ║ PARTIAL STATES                                                                                ║
// ╚═══════════════════════════════════════════════════════════════════════════════════════════════╝

sealed class GetSportsPartialState {
    data class Success(val sports: List<Sport>) : GetSportsPartialState()
    data class Failed(val errorMessage: String) : GetSportsPartialState()
}