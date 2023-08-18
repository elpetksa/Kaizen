package com.elpet.kaizen.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.elpet.kaizen.core.AppLogger
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ### ABOUT ###
 * Abstract class that is a superset of [Fragment]. Contains pre-defined variables and functions to
 * assist you and avoid boilerplate code.
 *
 *
 * ### VIEW BINDING ###
 * Base fragment class required a [ViewDataBinding] object as a parameter. Combined with [getLayout]
 * this class is able to initialize view binding and return the proper view when requested. You can
 * later on refer to that view binding by getting [binding]. You can not assign a value and you do
 * not have to. All required operations are performed here. Finally, view binding is automatically
 * cleared when [onDestroyView] is called.
 *
 * ### INJECTION ###
 * Base fragment class is using some services that must be injected at the initialization of this
 * fragment. Since an abstract can not hold required annotations, parent fragments that inherit
 * this class should use the `@AndroidEntryPoint` as shown below :
 *
 * ```
 * @AndroidEntryPoint
 * class Fragment : BaseFragment<VB, Event, State, Effect>() {
 *    ...
 * }
 * ```
 *
 * @param VB The binding class this fragment uses for layout.
 */
abstract class BaseFragment<VB: ViewBinding, Event: BaseEvent, State: BaseState, Effect: BaseEffect>: Fragment() {

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ VARIABLES                                                                                 ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    // ┌───────────────────────────────────────────────────────────────────────────────────────────┐
    //   → PRIVATE VARIABLES
    // └───────────────────────────────────────────────────────────────────────────────────────────┘

    /**
     * Layout binding.
     **/
    private var _binding: VB? = null

    /**
     * Defines if this fragments overrides parent activity back button press events. Required for
     * [onBackButtonPressed] operation to work.
     */
    open var overrideBackButton: Boolean = false

    // ┌───────────────────────────────────────────────────────────────────────────────────────────┐
    //   → PUBLIC VARIABLES
    // └───────────────────────────────────────────────────────────────────────────────────────────┘

    /**
     * [AppLogger] instance.
     */
    @Inject
    lateinit var logger: AppLogger

    /**
     * Layout binding. Never null when lifecycle between [onCreateView] and onDestroy.
     */
    val binding get() = _binding!!

    /**
     * View base view model.
     */
    abstract val viewModel: BaseViewModel<Event, State, Effect>

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ FUNCTIONS                                                                                 ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    // ┌───────────────────────────────────────────────────────────────────────────────────────────┐
    //   → OVERRIDE FUNCTIONS
    // └───────────────────────────────────────────────────────────────────────────────────────────┘

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null. This will be called between
     * [onCreate] and [onViewCreated].
     * A default View can be returned by calling [Fragment] in your
     * constructor. Otherwise, this method returns null.
     *
     * It is recommended to ***only*** inflate the layout in this method and move
     * logic that operates on the returned View to [onViewCreated].
     *
     * If you return a View from here, you will later be called in [onDestroyView] when the view is
     * being released.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        logger.d("[${javaClass.simpleName}] :: onCreateView")

        // Check if resource file is valid.
        if (getLayout() == 0)
            throw RuntimeException("Layout resource id ${getLayout()} is not valid. A valid " +
                    "resource retrieved from getLayout() is required to initialize binding.")

        // Initialize binding.
        _binding = DataBindingUtil.inflate(inflater, getLayout(), container, false)

        return binding.root
    }

    /**
     * Called immediately after [onCreateView] has returned, but before any saved state has been
     * restored in to the view. This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     * @param view The View returned by [onCreateView].
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        logger.d("[${javaClass.simpleName}] :: onViewCreated")

        // Initialize view.
        initView()

        // Start collecting state events.
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                consumeState(state)
            }
        }

        // Start collecting state effects.
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.effect.collect { effect ->
                consumeEffect(effect)
            }
        }

        // Register to back button dispatcher.
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(overrideBackButton) {
                override fun handleOnBackPressed() {
                    onBackButtonPressed()
                }
            }
        )
    }

    /**
     * Called when the view previously created by [onCreateView] has
     * been detached from the fragment.  The next time the fragment needs
     * to be displayed, a new view will be created.  This is called
     * after [onStop] and before [onDestroy].  It is called
     * _regardless_ of whether [onCreateView] returned a
     * non-null view.  Internally it is called after the view's state has
     * been saved but before it has been removed from its parent.
     */
    @CallSuper
    override fun onDestroyView() {

        logger.d("[${javaClass.simpleName}] :: onDestroyView")

        // Clear binding.
        _binding = null

        super.onDestroyView()
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     *
     * Derived classes should call through to the base class for it to
     * perform the default menu handling.
     *
     * @param item The menu item that was selected.
     *
     * @return boolean Return false to allow normal menu processing to
     *         proceed, true to consume it here.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId == android.R.id.home) {
            true -> {
                if (overrideBackButton) {
                    onBackButtonPressed()
                    return true
                }
                super.onOptionsItemSelected(item)
            }
            false -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    // ┌───────────────────────────────────────────────────────────────────────────────────────────┐
    //   → ABSTRACT FUNCTIONS
    // └───────────────────────────────────────────────────────────────────────────────────────────┘

    /**
     * Returns the layout resource file id used to initialize data view binding for this fragment.
     * You should always return a valid resource file and never return a 0 value here.
     *
     * @return The layout resource file that will be used to initialize data view binding.
     */
    @LayoutRes
    abstract fun getLayout(): Int

    /**
     * Called immediately after this view is created but before any saved state has been
     * restored in to the view. The view is already attached to the activity at this point. You
     * should initialize view models, variables, services etc. here.
     */
    abstract fun initView()

    /**
     * Notified every time view state is updated. You should check the new state and update the
     * view based on the new state values.
     *
     * @param state The new state of the view.
     */
    abstract fun consumeState(state: State)

    /**
     * Notified every time an [Effect] is emitted. You should perform corresponding actions on your
     * view based on the arrived effect.
     *
     * @param effect The effect that was emitted.
     */
    abstract fun consumeEffect(effect: Effect)

    /**
     * Invokes every time the user presses the back button or when the onBackButtonPressed
     * event is notified. You can override this function to perform custom operations on back
     * callbacks.
     *
     * ### INFO
     *
     * Notice that you should also override the [overrideBackButton] and return a
     * `true` value in order for this operation to work properly.
     */
    open fun onBackButtonPressed() {

    }

}