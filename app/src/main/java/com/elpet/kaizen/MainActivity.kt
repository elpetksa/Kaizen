package com.elpet.kaizen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.elpet.kaizen.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ VARIABLES                                                                                 ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    // ┌───────────────────────────────────────────────────────────────────────────────────────────┐
    //   → PRIVATE VARIABLES
    // └───────────────────────────────────────────────────────────────────────────────────────────┘

    /**
     * Layout binding.
     */
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ FUNCTIONS                                                                                 ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    // ┌───────────────────────────────────────────────────────────────────────────────────────────┐
    //   → OVERRIDE FUNCTIONS
    // └───────────────────────────────────────────────────────────────────────────────────────────┘

    /**
     * Called when the activity is starting.  This is where most initialization
     * should go: calling [setContentView] to inflate the
     * activity's UI, using {@link #findViewById} to programmatically interact
     * with widgets in the UI, calling
     * [managedQuery] to retrieve
     * cursors for data being displayed, etc.
     *
     * You can call [finish] from within this function, in
     * which case [onDestroy] will be immediately called after [onCreate] without any of the
     * rest of the activity lifecycle ([onStart], [onResume], [onPause] etc)
     * executing.
     *
     * Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in onSaveInstanceState.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set activity view.
        setContentView(binding.root)
    }
}