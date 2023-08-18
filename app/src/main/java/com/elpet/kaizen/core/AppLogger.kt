package com.elpet.kaizen.core

import com.orhanobut.logger.Logger

interface AppLogger {

    /**
     * Send a DEBUG log message.
     *
     * @param message The message you would like logged.
     */
    fun d(message: String)

    /**
     * Send an ERROR log message.
     *
     * @param message The message you would like logged.
     */
    fun e(message: String)

    /**
     * Send a ERROR log message and log the exception.
     *
     * @param message   The message you would like logged.
     * @param throwable An exception to log
     */
    fun e(message: String, throwable: Throwable?)

    /**
     * Send an WARNING log message.
     *
     * @param message The message you would like logged.
     */
    fun w(message: String)

}

class AppLoggerImpl: AppLogger {

    /**
     * Send a DEBUG log message.
     *
     * @param message The message you would like logged.
     */
    override fun d(message: String) {
        Logger.d(message)
    }

    /**
     * Send an ERROR log message.
     *
     * @param message The message you would like logged.
     */
    override fun e(message: String) {
        Logger.e(message)
    }

    /**
     * Send a ERROR log message and log the exception.
     *
     * @param message   The message you would like logged.
     * @param throwable An exception to log
     */
    override fun e(message: String, throwable: Throwable?) {
        Logger.e(throwable, message)
    }

    /**
     * Send an WARNING log message.
     *
     * @param message The message you would like logged.
     */
    override fun w(message: String) {
        Logger.w(message)
    }

}