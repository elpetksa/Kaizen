package com.elpet.kaizen.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class BaseViewModel<Event: BaseEvent, State: BaseState, Effect: BaseEffect> : ViewModel() {

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ VARIABLES                                                                                 ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    // ┌───────────────────────────────────────────────────────────────────────────────────────────┐
    //   → PRIVATE VARIABLES
    // └───────────────────────────────────────────────────────────────────────────────────────────┘

    /**
     * View model initial state.
     */
    private val initialState: State by lazy { setInitialState() }

    /**
     * Mutable state flow of this view model. Used to dispatch states for view.
     */
    private var _state: MutableStateFlow<State> = MutableStateFlow(initialState)

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()

    private val _effect: Channel<Effect> = Channel()

    // ┌───────────────────────────────────────────────────────────────────────────────────────────┐
    //   → PUBLIC VARIABLES
    // └───────────────────────────────────────────────────────────────────────────────────────────┘

    /**
     * Current view model state. Use this to make a copy out of it in order to keep current values
     * and only change the values you are interested at.
     */
    var state: StateFlow<State> = _state.asStateFlow()

    /**
     * The effect flow. Used from [BaseFragment] to collect effects and invoke
     * [BaseFragment.consumeEffect]. No use for custom implementation.
     */
    val effect: Flow<Effect> = _effect.receiveAsFlow()

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ FUNCTIONS                                                                                 ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    // ┌───────────────────────────────────────────────────────────────────────────────────────────┐
    //   → PRIVATE FUNCTIONS
    // └───────────────────────────────────────────────────────────────────────────────────────────┘

    init {
        subscribeToEvents()
    }

    private fun subscribeToEvents() {
        viewModelScope.launch {
            _event.collect {
                handleEvents(it)
            }
        }
    }

    // ┌───────────────────────────────────────────────────────────────────────────────────────────┐
    //   → ABSTRACT FUNCTIONS
    // └───────────────────────────────────────────────────────────────────────────────────────────┘

    /**
     * Sets the initial [BaseState] for this view model. The state that is returned from this
     * function will be the initial state of the model. Use this to reset a view when navigating
     * to that view.
     *
     * @return A [BaseState] that is used as the initial state of this view.
     */
    abstract fun setInitialState(): State

    /**
     * Invokes every time a new [BaseEvent] is emitted. You should perform corresponding operations
     * based on the event received. Events are collectd on [viewModelScope].
     *
     * @param event The [BaseEvent] that was emitted.
     */
    abstract fun handleEvents(event: Event)

    /**
     * Emit a new event. Corresponding collector will receive the emitted event and perform
     * corresponding actions. Most of the times, the collector is the view model it self that will
     * collect the emitted event and perform an operation. For example, you can have a data class
     * event to perform a login operation. The view model will collect that event and perform
     * actions required.
     *
     * You are strongly advised to use the events only to perform actions on the view model or
     * an interactor. Notice that events are emitted at [viewModelScope].
     *
     * @param event The event to emit.
     */
    fun setEvent(event: Event) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }

    /**
     * Set a new state for the view model. Corresponding collector will receive the new state and
     * perform corresponding operations. Most of the times, the view is the collector of these
     * states. You can set a new state to update a variable for example. You must not use state
     * system to show a toast etc. For that reason, you can use the [setEffect] operation.
     *
     * Notice that setting a new state will erase the previous data. This is like resetting the
     * whole view model. You should consider copying the current state and updating only the values
     * that you are interested at.
     *
     * Take a look at the example below on how to set a new state from current state :
     *
     * `
     * setState(
     *     state.value.copy(
     *         loading = true
      *    )
     * )
     * `
     *
     * At the above example, we copy the current state of the view model and we only update this
     * `loading` value.
     */
    protected fun setState(state: State) {
        _state.value = state
    }

    /**
     * Set an effect to apply to view of this view model. An effect is like a reversed event that
     * is used from the model to set - let's say - a state to the view. For example, an effect
     * can be a request error or show an error message.
     */
    protected fun setEffect(builder: () -> Effect) {
        val effectValue = builder()
        viewModelScope.launch {
            _effect.send(effectValue)
        }
    }

}
