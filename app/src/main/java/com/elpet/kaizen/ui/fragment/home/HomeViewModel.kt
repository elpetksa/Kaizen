package com.elpet.kaizen.ui.fragment.home

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.elpet.kaizen.R
import com.elpet.kaizen.data.database.FavoriteEventsDatabase
import com.elpet.kaizen.data.model.local.Event
import com.elpet.kaizen.data.model.toHomeSports
import com.elpet.kaizen.ui.base.BaseEffect
import com.elpet.kaizen.ui.base.BaseEvent
import com.elpet.kaizen.ui.base.BaseState
import com.elpet.kaizen.ui.base.BaseViewModel
import com.elpet.kaizen.ui.fragment.home.models.HomeSport
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomeEffect : BaseEffect {
    data class ShowEmptyView(@StringRes val message: Int): HomeEffect()
    object HideEmptyView: HomeEffect()
    data class SetSports(val sports: List<HomeSport>): HomeEffect()
    data class ShowSportDialog(val title: String, val copyData: String, val shareData: String): HomeEffect()
    data class EventUpdated(val event: Event): HomeEffect()
}

sealed class HomeEvent : BaseEvent {
    object Init : HomeEvent()
    object ReloadSports : HomeEvent()
    data class EventClicked(val event: Event): HomeEvent()
    data class EventFavoriteClick(val event: Event): HomeEvent()
}

data class HomeState(
    val loading: Boolean = false,
) : BaseState

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val interactor: HomeInteractor,
    private val favoriteEventsDatabase: FavoriteEventsDatabase
) : BaseViewModel<HomeEvent, HomeState, HomeEffect>() {

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ FUNCTIONS                                                                                 ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    // ┌───────────────────────────────────────────────────────────────────────────────────────────┐
    //   → OVERRIDE FUNCTIONS
    // └───────────────────────────────────────────────────────────────────────────────────────────┘

    /**
     * Sets the initial [BaseState] for this view model. The state that is returned from this
     * function will be the initial state of the model. Use this to reset a view when navigating
     * to that view.
     *
     * @return A [BaseState] that is used as the initial state of this view.
     */
    override fun setInitialState(): HomeState = HomeState(loading = false)

    /**
     * Invokes every time a new [BaseEvent] is emitted. You should perform corresponding operations
     * based on the event received. Events are collectd on [viewModelScope].
     *
     * @param event The [BaseEvent] that was emitted.
     */
    override fun handleEvents(event: HomeEvent) {
        when (event) {
            is HomeEvent.Init -> {
                loadSports()
            }

            is HomeEvent.ReloadSports -> {
                loadSports()
            }

            is HomeEvent.EventClicked -> {
                setEffect {
                    HomeEffect.ShowSportDialog(
                        title = event.event.name,
                        copyData = event.event.name,
                        shareData = event.event.name
                    )
                }
            }

            is HomeEvent.EventFavoriteClick -> {
                eventFavoriteClick(
                    event = event.event
                )
            }
        }
    }

    /**
     * Fetches sports and corresponding events from server. Does not clear the database but does
     * update it if new sports arrive successfully.
     */
    private fun loadSports() {
        // Start loading.
        setState(
            state.value.copy(
                loading = true
            )
        )

        // Get sports from server.
        viewModelScope.launch {
            interactor.getSports().collect {
                // Stop loading.
                setState(
                    state.value.copy(
                        loading = false
                    )
                )

                // Manipulate response result.
                when (it) {
                    is GetSportsPartialState.Success -> {
                        setEffect {
                            HomeEffect.SetSports(
                                sports = it.sports.toHomeSports()
                            )
                        }
                        setEffect {
                            HomeEffect.HideEmptyView
                        }
                    }
                    is GetSportsPartialState.Failed -> {
                        setEffect {
                            HomeEffect.ShowEmptyView(
                                message = R.string.home_event_error_empty
                            )
                        }
                    }
                }
            }
        }
    }

    private fun eventFavoriteClick(event: Event) {
        // Check if event is favorite.
        val isFavorite = favoriteEventsDatabase.eventDao().getAll()
            .any { sportEvent -> sportEvent.id == event.id }

        // Toggle favorite flag.
        event.isFavorite = isFavorite.not()

        // Manipulate action.
        when (event.isFavorite) {
            true -> {
                // Add event to database.
                favoriteEventsDatabase.eventDao().insertAll(event)
            }

            false -> {
                // Remove event from database.
                favoriteEventsDatabase.eventDao().delete(event)
            }
        }

        // Update sport.
        setEffect {
            HomeEffect.EventUpdated(
                event = event
            )
        }
    }
}