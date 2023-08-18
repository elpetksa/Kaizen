package com.elpet.kaizen.ui.fragment.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

import android.os.Handler
import android.os.Looper
import androidx.navigation.Navigation
import com.elpet.kaizen.core.AppLogger
import com.elpet.kaizen.databinding.FragmentSplashBinding
import com.elpet.kaizen.util.SPLASH_DURATION

@AndroidEntryPoint
class SplashFragment: Fragment() {

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ VARIABLES                                                                                 ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    // ┌───────────────────────────────────────────────────────────────────────────────────────────┐
    //   → PRIVATE VARIABLES
    // └───────────────────────────────────────────────────────────────────────────────────────────┘

    /**
     * Layout binding.
     **/
    private var _binding: FragmentSplashBinding? = null

    /**
     * Layout binding. Never null when lifecycle between [onCreateView] and onDestroy.
     */
    private val binding get() = _binding!!

    /**
     * Timer handler.
     */
    private lateinit var handler: Handler

    /**
     * Timed runnable.
     */
    private lateinit var runnable: Runnable

    /**
     * [AppLogger] instance.
     */
    @Inject
    internal lateinit var logger: AppLogger

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
     * @param container If non-null, this is the parent view that the fragment"s
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

        // Initialize binding.
        _binding = FragmentSplashBinding.inflate(inflater, container, false)

        return binding.root
    }

    /**
     * Called when the Fragment is resumed. This is generally
     * tied to `Activity.onResume` of the containing
     * Activity's lifecycle.
     */
    override fun onResume() {
        super.onResume()

        logger.d("[${javaClass.simpleName}] :: onResume called.")

        // Initialize handler.
        handler = Handler(Looper.getMainLooper())

        // Initialize runnable.
        runnable = Runnable {
            Navigation.findNavController(binding.root)
                .navigate(SplashFragmentDirections.actionSplashFragmentToNavigationHome())
        }

        // Start timer.
        handler.postDelayed(runnable, SPLASH_DURATION)
    }

    /**
     * Called when the Fragment is no longer resumed.  This is generally
     * tied to `Activity.onPause` of the containing
     * Activity's lifecycle.
     */
    override fun onPause() {
        super.onPause()

        // Cancel handler.
        handler.removeCallbacks(runnable)
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

}