package com.elpet.kaizen.ui.fragment.home

import android.annotation.SuppressLint
import android.content.Intent
import android.text.format.DateUtils
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.elpet.kaizen.R
import com.elpet.kaizen.data.model.local.Event
import com.elpet.kaizen.databinding.DialogSportEventBinding
import com.elpet.kaizen.databinding.ElementEventHolderBinding
import com.elpet.kaizen.databinding.ElementSportHolderBinding
import com.elpet.kaizen.databinding.FragmentHomeBinding
import com.elpet.kaizen.ui.base.BaseEffect
import com.elpet.kaizen.ui.base.BaseFragment
import com.elpet.kaizen.ui.fragment.home.models.HomeSport
import com.elpet.kaizen.util.easyAdapter.EasyAdapter
import com.elpet.kaizen.util.extensions.collapse
import com.elpet.kaizen.util.extensions.copyToClipboard
import com.elpet.kaizen.util.extensions.expand
import com.elpet.kaizen.util.extensions.gone
import com.elpet.kaizen.util.extensions.show
import com.elpet.kaizen.util.extensions.showElseGone
import com.elpet.kaizen.util.extensions.toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Timer
import kotlin.concurrent.timer
import kotlin.math.abs
import kotlin.random.Random

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeEvent, HomeState, HomeEffect>(), EventHolderListener {

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ VARIABLES                                                                                 ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    // ┌───────────────────────────────────────────────────────────────────────────────────────────┐
    //   → PRIVATE VARIABLES
    // └───────────────────────────────────────────────────────────────────────────────────────────┘

    /**
     * View base view model.
     */
    override val viewModel: HomeViewModel by viewModels()

    /**
     * Sports recycler view adapter.
     */
    private val sportsAdapter: EasyAdapter<HomeSport, SportViewHolder> =
        EasyAdapter(R.layout.element_sport_holder) { view -> SportViewHolder(view, this) }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ FUNCTIONS                                                                                 ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    // ┌───────────────────────────────────────────────────────────────────────────────────────────┐
    //   → OVERRIDE FUNCTIONS
    // └───────────────────────────────────────────────────────────────────────────────────────────┘

    /**
     * Returns the layout resource file id used to initialize data view binding for this fragment.
     * You should always return a valid resource file and never return a 0 value here.
     *
     * @return The layout resource file that will be used to initialize data view binding.
     */
    override fun getLayout(): Int = R.layout.fragment_home

    /**
     * Called immediately after this view is created but before any saved state has been
     * restored in to the view. The view is already attached to the activity at this point. You
     * should initialize view models, variables, services etc. here.
     */
    override fun initView() {

        // Set recycler view linear manager.
        binding.sportsList.layoutManager = LinearLayoutManager(requireContext())

        // Set adapter to recycler view.
        binding.sportsList.adapter = sportsAdapter

        // Set swipe refresh callback.
        binding.sportsListRefresh.setOnRefreshListener {
            viewModel.setEvent(
                HomeEvent.ReloadSports
            )
        }

        // Set empty view retry button click listener.
        binding.emptyView.retryButton.setOnClickListener {
            viewModel.setEvent(
                HomeEvent.ReloadSports
            )
        }

        // Initialize view model.
        viewModel.setEvent(
            HomeEvent.Init
        )
    }

    /**
     * Notified every time view state is updated. You should check the new state and update the
     * view based on the new state values.
     *
     * @param state The new state of the view.
     */
    override fun consumeState(state: HomeState) {

        logger.d("[${javaClass.simpleName}] :: New state consumed : $state")

        // Toggle loading.
        binding.loading.root.showElseGone { state.loading }

        // Stop swipe refresh.
        if (state.loading.not()) {
            binding.sportsListRefresh.isRefreshing = false
        }
    }

    /**
     * Notified every time an [BaseEffect] is emitted. You should perform corresponding
     * actions on your view based on the arrived effect.
     *
     * @param effect The effect that was emitted.
     */
    override fun consumeEffect(effect: HomeEffect) {

        logger.d("[${javaClass.simpleName}] :: New effect consumed : $effect")

        when (effect) {
            is HomeEffect.SetSports -> {
                // Set sports.
                sportsAdapter.setData(
                    data = effect.sports
                )
            }

            is HomeEffect.ShowEmptyView -> {
                // Show empty view visibility.
                binding.emptyView.root.show()
                binding.sportsListRefresh.gone()

                // Show empty message.
                binding.emptyView.emptyText.setText(effect.message)
            }

            is HomeEffect.HideEmptyView -> {
                // Show empty view visibility.
                binding.emptyView.root.gone()
                binding.sportsListRefresh.show()
            }

            is HomeEffect.ShowSportDialog -> {
                // Initialize dialog and content view.
                val dialog = BottomSheetDialog(requireContext())
                val dialogView = DialogSportEventBinding.inflate(layoutInflater)

                // Initialize text of bottom sheet.
                dialogView.title.text = effect.title

                // Set listeners.
                dialogView.copy.setOnClickListener {
                    effect.copyData.copyToClipboard(binding.root.context)
                    context?.toast(R.string.generic_copied)
                    dialog.dismiss()
                }
                dialogView.share.setOnClickListener {
                    val intent = Intent()
                    intent.action = Intent.ACTION_SEND
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_TEXT, effect.shareData)
                    startActivity(Intent.createChooser(intent, context?.getString(R.string.generic_share)))
                    dialog.dismiss()
                }

                // Set content view.
                dialog.setContentView(dialogView.root)

                // Show the dialog.
                dialog.show()
            }

            is HomeEffect.EventUpdated -> {
                // Find sport view holder.
                val holder = sportsAdapter.viewHolders
                    .firstOrNull { sportHolder ->
                        sportHolder.data?.sport?.events?.any { event -> event.id == effect.event.id } ?: false
                    } ?: return

                // Find index of event.
                holder.eventsAdapter.updateData(effect.event)
            }
        }

    }

    // ┌───────────────────────────────────────────────────────────────────────────────────────────┐
    //   → EVENT HOLDER LISTENER FUNCTIONS
    // └───────────────────────────────────────────────────────────────────────────────────────────┘

    /**
     * Invokes when user clicks on a sport card.
     *
     * @param event Corresponding sport that was clicked.
     */
    override fun onEventClicked(event: Event) {
        viewModel.setEvent(
            HomeEvent.EventClicked(
                event = event
            )
        )
    }

    /**
     * Invokes when user clicks the favorite button of the event.
     *
     * @param event Corresponding event that was clicked.
     */
    override fun onEventFavoriteClicked(event: Event) {
        viewModel.setEvent(
            HomeEvent.EventFavoriteClick(
                event = event
            )
        )
    }
}

// ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
// ║ SPORT HOLDER CLASS                                                                        ║
// ╚═══════════════════════════════════════════════════════════════════════════════════════════╝

/**
 * View holder for sports.
 */
class SportViewHolder(val view: View, private val listener: EventHolderListener)
    : EasyAdapter.EasyAdapterViewHolder<HomeSport>(view) {

    /**
     * Layout binding.
     */
    private val binding = ElementSportHolderBinding.bind(view)

    /**
     * Events recycler view adapter.
     */
    val eventsAdapter: EasyAdapter<Event, EventViewHolder> =
        EasyAdapter(R.layout.element_event_holder) { view -> EventViewHolder(view, listener) }

    /**
     * Called when the adapter needs to bind this view holder with a data object. Initialize
     * your view here.
     *
     * @param data     Corresponding data to bind with this view.
     * @param position The position of this view holder. Used to identify if last etc.
     * @param total    Defines how many items adapter currently has.
     */
    @SuppressLint("SetTextI18n")
    override fun bindViewHolder(data: HomeSport, position: Int, total: Int) {
        // Set adapter to recycler view.
        binding.eventsList.adapter = eventsAdapter

        // Set label.
        binding.sportName.text = data.sport.name

        // Set toggle listener.
        binding.filterFavorites.setOnCheckedChangeListener { _, isChecked ->
            eventsAdapter.setData(
                data = filterData(
                    events = data.sport.events,
                    filterFavorites = isChecked
                )
            )
            binding.eventsList.expand(
                rotateView = binding.arrow,
                duration = 0L
            )
        }

        // Init collapse.
        when (data.isExpanded) {
            true -> {
                binding.eventsList.expand(
                    rotateView = binding.arrow,
                    duration = 0L
                )
            }

            false -> {
                binding.eventsList.collapse(
                    rotateView = binding.arrow,
                    duration = 0L
                )
            }
        }

        // Set root click listener to toggle collapse.
        binding.root.setOnClickListener {
            when (data.isExpanded) {
                true -> {
                    binding.eventsList.collapse(
                        rotateView = binding.arrow
                    )
                }

                false -> {
                    // Populate adapter.
                    eventsAdapter.setData(
                        data = filterData(
                            events = data.sport.events,
                            filterFavorites = binding.filterFavorites.isChecked
                        )
                    )

                    binding.eventsList.expand(
                        rotateView = binding.arrow
                    )
                }
            }
            data.isExpanded = data.isExpanded.not()
        }
    }

    /**
     * Filters events based on given filtering options.
     *
     * @param events Base events list to filter.
     * @param filterFavorites Only return favorite events.
     *
     * @return List of [Event] filtered based on given options.
     */
    private fun filterData(events: List<Event>, filterFavorites: Boolean): List<Event> {
        if (filterFavorites.not()) return events
        return events.filter { event -> event.isFavorite }
    }
}

// ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
// ║ EVENT HOLDER CLASS                                                                        ║
// ╚═══════════════════════════════════════════════════════════════════════════════════════════╝

class EventViewHolder(val view: View, private val listener: EventHolderListener)
    : EasyAdapter.EasyAdapterViewHolder<Event>(view) {

    /**
     * Layout binding.
     */
    private val binding = ElementEventHolderBinding.bind(view)

    /**
     * Timer to update date.
     */
    private lateinit var timer: Timer

    /**
     * Called when the adapter needs to bind this view holder with a data object. Initialize
     * your view here.
     *
     * @param data     Corresponding data to bind with this view.
     * @param position The position of this view holder. Used to identify if last etc.
     * @param total    Defines how many items adapter currently has.
     */
    @SuppressLint("SetTextI18n")
    override fun bindViewHolder(data: Event, position: Int, total: Int) {
        // Set root click listener.
        binding.root.setOnClickListener {
            listener.onEventClicked(
                event = data
            )
        }

        // Set favorite click listener.
        binding.favorite.setOnClickListener {
            listener.onEventFavoriteClicked(
                event = data
            )
        }

        // Toggle favorite icon.
        binding.favorite.setImageResource(
            when (data.isFavorite) {
                true -> {
                    R.drawable.baseline_star_24
                }

                false -> {
                    R.drawable.baseline_star_border_24
                }
            }
        )

        // Get teams.
        val teams = data.name.split('-')

        // Set tournament.
        binding.tournament.text = data.tournament
        binding.tournament.showElseGone { data.tournament.isNotEmpty() }

        // Set odd.
        binding.odd.text = "1:${DecimalFormat("#.#").format(Random.nextDouble(0.1, 10.0))}"

        // Cancel previous timers.
        if (this::timer.isInitialized) timer.cancel()

        // Set time text.
        timer = timer(name = data.id, period = 1000L) {
            // Set some flags.
            val isPast = System.currentTimeMillis() / 1000 > data.startTime
            val diff = abs(System.currentTimeMillis() / 1000 - data.startTime)

            // Back to main thread ofc :)
            view.post {
                // If span is longer than 24 hours, show date.
                when (diff > 24 * 60 * 60) {
                    true -> {
                        binding.timer.text = DateTimeFormatter.ofPattern("dd/MM").format(
                            LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(data.startTime * 1000L),
                                ZoneId.systemDefault()
                            )
                        )
                    }

                    else -> {
                        when (isPast) {
                            true -> {
                                binding.timer.text = DateUtils.getRelativeTimeSpanString(
                                    System.currentTimeMillis(),
                                    data.startTime * 1000L,
                                    60000
                                )
                            }

                            false -> {
                                binding.timer.text =
                                    DateUtils.formatElapsedTime(diff)
                            }
                        }
                    }
                }
            }
        }

        // Set team A.
        binding.teamA.text = teams.elementAtOrNull(0) ?: "-"

        // Set team B.
        binding.teamB.text = teams.elementAtOrNull(1) ?: "-"
    }
}

interface EventHolderListener {
    /**
     * Invokes when user clicks on an event card.
     *
     * @param event Corresponding event that was clicked.
     */
    fun onEventClicked(event: Event)

    /**
     * Invokes when user clicks the favorite button of the event.
     *
     * @param event Corresponding event that was clicked.
     */
    fun onEventFavoriteClicked(event: Event)
}