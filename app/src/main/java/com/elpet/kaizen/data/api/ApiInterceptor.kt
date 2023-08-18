package com.elpet.kaizen.data.api

import android.content.Context
import com.elpet.kaizen.util.extensions.hasNetworkConnectivity
import com.elpet.kaizen.R
import com.elpet.kaizen.core.AppLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor class used from [ApiService] controllers to intercept requests. All required API
 * headers are added here. Also validates if there's network connection and throws exception with localized message.
 * Notice that Authorization header is manipulated at [ApiAuthenticatorImpl] and not here.
 */
@Singleton
class ApiInterceptor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val logger: AppLogger,
) : Interceptor {

    /**
     * Observes, modifies, and potentially short-circuits requests going out and the corresponding
     * responses coming back in. Typically interceptors add, remove, or transform headers on the request
     * or response.
     *
     * Implementations of this interface throw [IOException] to signal connectivity failures. This
     * includes both natural exceptions such as unreachable servers, as well as synthetic exceptions
     * when responses are of an unexpected type or cannot be decoded.
     *
     * Other exception types cancel the current call:
     *
     *  * For synchronous calls made with [Call.execute], the exception is propagated to the caller.
     *
     *  * For asynchronous calls made with [Call.enqueue], an [IOException] is propagated to the caller
     *    indicating that the call was canceled. The interceptor's exception is delivered to the current
     *    thread's [uncaught exception handler][Thread.UncaughtExceptionHandler]. By default this
     *    crashes the application on Android and prints a stacktrace on the JVM. (Crash reporting
     *    libraries may customize this behavior.)
     */
    override fun intercept(chain: Interceptor.Chain): Response {

        // Check if we have access to the internet.
        if (!context.hasNetworkConnectivity())
            throw IOException(context.getString(R.string.generic_network_error_message))

        // Initialize a new request builder based on current one.
        val requestBuilder = chain.request().newBuilder()

        // Add other required headers.
        requestBuilder.addHeader("Accept", "application/json")
        requestBuilder.addHeader("Content-Type", "application/json")

        return chain.proceed(requestBuilder.build().apply {
            logger.d("[${javaClass.name}] :: ${toString()}")
        })
    }
}