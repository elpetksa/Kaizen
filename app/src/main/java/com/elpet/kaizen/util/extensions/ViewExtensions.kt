package com.elpet.kaizen.util.extensions

import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import android.view.animation.Animation
import android.view.animation.Transformation

/**
 * Changes the visibility of this view to [View.VISIBLE]
 *
 * @see [hide]
 * @see [gone]
 */
fun View.show() {
    visibility = View.VISIBLE
}

/**
 * Changes the visibility of this view to [View.INVISIBLE]
 *
 * @see [show]
 * @see [gone]
 */
fun View.hide() {
    visibility = View.INVISIBLE
}

/**
 * Changes the visibility of this view to [View.GONE]
 *
 * @see [show]
 * @see [hide]
 */
fun View.gone() {
    visibility = View.GONE
}

/**
 * Makes this view [View.VISIBLE] if given predicate requirements are met. If not, view visibility
 * is set to [View.GONE].
 *
 * @param predicate Predicate to show if matches of set to gone otherwise.
 */
inline fun View.showElseGone(predicate: () -> Boolean) {
    if (predicate.invoke()) show()
    else gone()
}

/**
 * Makes this view [View.VISIBLE] if given predicate requirements are met. If not, view visibility
 * is set to [View.INVISIBLE].
 *
 * @param predicate Predicate to show if matches of set to invisible otherwise.
 */
inline fun View.showElseInvisible(predicate: () -> Boolean) {
    if (predicate.invoke()) show()
    else hide()
}

/**
 * Smoothly expands this view to match the content height by changing the height and the alpha of
 * the view over time. You can hide (collapse) a view on the other hand by calling [collapse].
 *
 * You can also pass a view here to rotate based on expansion state.
 *
 * @param rotateView View to rotate.
 * @param rotation   Final rotation value.
 */
fun View.expand(
    rotateView: View? = null,
    rotation: Int = 90,
    duration: Long = 400L
) {
    val matchParentMeasureSpec =
        View.MeasureSpec.makeMeasureSpec((parent as View).width, View.MeasureSpec.EXACTLY)
    val wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    measure(matchParentMeasureSpec, wrapContentMeasureSpec)

    // Calculate target height.
    val targetHeight = measuredHeight

    // Older versions of android (pre API 21) cancel animations for views with a height of 0.
    layoutParams.height = 1

    // Show view.
    show()

    val animation: Animation = object : Animation() {
        override fun applyTransformation(time: Float, t: Transformation?) {
            rotateView?.let {
                it.rotation = time * rotation
            }
            layoutParams.height =
                when (time == 1f) {
                    true -> targetHeight
                    false -> (targetHeight * time).toInt()
                }
            alpha = time
            requestLayout()
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    // Set animation duration.
    animation.duration = duration

    // Start animation.
    startAnimation(animation)
}

/**
 * Smoothly collapses this view to 0 height by changing the height and the alpha of
 * the view over time. You can show (expand) a view on the other hand by calling [expand].
 *
 * You can also pass a view here to rotate based on collapse state.
 *
 * @param rotateView View to rotate.
 * @param rotation   Final rotation value.
 */
fun View.collapse(
    rotateView: View? = null,
    rotation: Int = -90,
    duration: Long = 400L
) {
    // Get a snapshot of current height.
    val initialHeight = measuredHeight
    
    // Initialize animation.
    val animation: Animation = object : Animation() {
        override fun applyTransformation(time: Float, t: Transformation?) {
            rotateView?.let {
                it.rotation = time * rotation
            }
            if (time == 1f) gone()
            else {
                layoutParams.height = initialHeight - (initialHeight * time).toInt()
                requestLayout()
            }
            alpha = 1 - time
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    // Set animation duration.
    animation.duration = duration

    // Start animation.
    startAnimation(animation)
}