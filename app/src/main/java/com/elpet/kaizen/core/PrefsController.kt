package com.elpet.kaizen.core

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Controller used to manipulate data stored in device [SharedPreferences]. Data are encrypted so
 * you are strongly advised to use this controller to set or get values.
 */
interface PrefsController {

    /**
     * Defines if [SharedPreferences] contains a value for given [key]. This function will only
     * identify if a key exists in storage and will not check if corresponding value is valid.
     *
     * @param key The name of the preference to check.
     *
     * @return `true` if preferences contain given key. `false` otherwise.
     */
    fun contains(key: String): Boolean

    /**
     * Removes given preference key from shared preferences. Notice that this operation is
     * irreversible and may lead to data loss.
     */
    fun clear(key: String)

    /**
     * Removes all keys from shared preferences. Notice that this operation is
     * irreversible and may lead to data loss.
     */
    fun clear()

    /**
     * Assigns given [value] to device storage - shared preferences given [key]. You can
     * retrieve this value by calling [getString].
     *
     * Shared preferences are encrypted. Do not create your own instance to add or retrieve data.
     * Instead, call operations of this controller.
     *
     * @param key   Key used to add given [value].
     * @param value Value to add after given [key].
     */
    fun setString(key: String, value: String)

    /**
     * Assigns given [value] to device storage - shared preferences given [key]. You can
     * retrieve this value by calling [getString].
     *
     * Shared preferences are encrypted. Do not create your own instance to add or retrieve data.
     * Instead, call operations of this controller.
     *
     * @param key   Key used to add given [value].
     * @param value Value to add after given [key].
     */
    fun setLong(
        key: String, value: Long
    )

    /**
     * Assigns given [value] to device storage - shared preferences given [key]. You can
     * retrieve this value by calling [getString].
     *
     * Shared preferences are encrypted. Do not create your own instance to add or retrieve data.
     * Instead, call operations of this controller.
     *
     * @param key   Key used to add given [value].
     * @param value Value to add after given [key].
     */
    fun setBool(key: String, value: Boolean)

    /**
     * Retrieves a string value from device shared preferences that corresponds to given [key]. If
     * key does not exist or value of given key is null, [defaultValue] is returned.
     *
     * Shared preferences are encrypted. Do not create your own instance to add or retrieve data.
     * Instead, call operations of this controller.
     *
     * @param key          Key to get corresponding value.
     * @param defaultValue Default value to return if given [key] does not exist in prefs or if
     * key value is invalid.
     */
    fun getString(key: String, defaultValue: String): String

    /**
     * Retrieves a long value from device shared preferences that corresponds to given [key]. If
     * key does not exist or value of given key is null, [defaultValue] is returned.
     *
     * Shared preferences are encrypted. Do not create your own instance to add or retrieve data.
     * Instead, call operations of this controller.
     *
     * @param key          Key to get corresponding value.
     * @param defaultValue Default value to return if given [key] does not exist in prefs or if
     * key value is invalid.
     */
    fun getLong(key: String, defaultValue: Long): Long

    /**
     * Retrieves a boolean value from device shared preferences that corresponds to given [key]. If
     * key does not exist or value of given key is null, [defaultValue] is returned.
     *
     * Shared preferences are encrypted. Do not create your own instance to add or retrieve data.
     * Instead, call operations of this controller.
     *
     * @param key          Key to get corresponding value.
     * @param defaultValue Default value to return if given [key] does not exist in prefs or if
     * key value is invalid.
     */
    fun getBool(key: String, defaultValue: Boolean): Boolean

}

/**
 * Implementation of [PrefsController].
 */
class PrefsControllerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PrefsController {

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ VARIABLES                                                                                 ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    // ┌───────────────────────────────────────────────────────────────────────────────────────────┐
    //   → PRIVATE VARIABLES
    // └───────────────────────────────────────────────────────────────────────────────────────────┘

    /**
     * Master key used to encrypt/decrypt shared preferences.
     */
    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    /**
     * Pref key scheme used to initialize [EncryptedSharedPreferences] instance.
     */
    private val prefKeyEncryptionScheme by lazy {
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV
    }

    /**
     * Pref value scheme used to initialize [EncryptedSharedPreferences] instance.
     */
    private val prefValueEncryptionScheme by lazy {
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    }

    /**
     * Initializes and returns a new [SharedPreferences] instance. Instance is using an encryption
     * to store data in device.
     *
     * @return A new [SharedPreferences] instance.
     */
    private fun getSharedPrefs(): SharedPreferences {
        return EncryptedSharedPreferences.create(
            context,
            "secret_shared_prefs",
            masterKey,
            prefKeyEncryptionScheme,
            prefValueEncryptionScheme
        )
    }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ FUNCTIONS                                                                                 ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    // ┌───────────────────────────────────────────────────────────────────────────────────────────┐
    //   → OVERRIDE FUNCTIONS
    // └───────────────────────────────────────────────────────────────────────────────────────────┘

    /**
     * Defines if [SharedPreferences] contains a value for given [key]. This function will only
     * identify if a key exists in storage and will not check if corresponding value is valid.
     *
     * @param key The name of the preference to check.
     *
     * @return `true` if preferences contain given key. `false` otherwise.
     */
    override fun contains(key: String): Boolean {
        return getSharedPrefs().contains(key)
    }

    /**
     * Removes given preference key from shared preferences. Notice that this operation is
     * irreversible and may lead to data loss.
     */
    override fun clear(key: String) {
        getSharedPrefs().edit().remove(key).apply()
    }

    /**
     * Removes all keys from shared preferences. Notice that this operation is
     * irreversible and may lead to data loss.
     */
    override fun clear() {
        getSharedPrefs().edit().clear().apply()
    }

    /**
     * Assigns given [value] to device storage - shared preferences given [key]. You can
     * retrieve this value by calling [getString].
     *
     * Shared preferences are encrypted. Do not create your own instance to add or retrieve data.
     * Instead, call operations of this controller.
     *
     * @param key   Key used to add given [value].
     * @param value Value to add after given [key].
     */
    override fun setString(key: String, value: String) {
        getSharedPrefs().edit()
            .putString(key, value)
            .apply()
    }

    /**
     * Assigns given [value] to device storage - shared preferences given [key]. You can
     * retrieve this value by calling [getString].
     *
     * Shared preferences are encrypted. Do not create your own instance to add or retrieve data.
     * Instead, call operations of this controller.
     *
     * @param key   Key used to add given [value].
     * @param value Value to add after given [key].
     */
    override fun setLong(
        key: String, value: Long
    ) {
        getSharedPrefs().edit()
            .putLong(key, value)
            .apply()
    }

    /**
     * Assigns given [value] to device storage - shared preferences given [key]. You can
     * retrieve this value by calling [getString].
     *
     * Shared preferences are encrypted. Do not create your own instance to add or retrieve data.
     * Instead, call operations of this controller.
     *
     * @param key   Key used to add given [value].
     * @param value Value to add after given [key].
     */
    override fun setBool(key: String, value: Boolean) {
        getSharedPrefs().edit()
            .putBoolean(key, value)
            .apply()
    }

    /**
     * Retrieves a string value from device shared preferences that corresponds to given [key]. If
     * key does not exist or value of given key is null, [defaultValue] is returned.
     *
     * Shared preferences are encrypted. Do not create your own instance to add or retrieve data.
     * Instead, call operations of this controller.
     *
     * @param key          Key to get corresponding value.
     * @param defaultValue Default value to return if given [key] does not exist in prefs or if
     * key value is invalid.
     */
    override fun getString(key: String, defaultValue: String): String {
        return getSharedPrefs().getString(key, defaultValue) ?: defaultValue
    }

    /**
     * Retrieves a long value from device shared preferences that corresponds to given [key]. If
     * key does not exist or value of given key is null, [defaultValue] is returned.
     *
     * Shared preferences are encrypted. Do not create your own instance to add or retrieve data.
     * Instead, call operations of this controller.
     *
     * @param key          Key to get corresponding value.
     * @param defaultValue Default value to return if given [key] does not exist in prefs or if
     * key value is invalid.
     */
    override fun getLong(key: String, defaultValue: Long): Long {
        return getSharedPrefs().getLong(key, defaultValue)
    }

    /**
     * Retrieves a boolean value from device shared preferences that corresponds to given [key]. If
     * key does not exist or value of given key is null, [defaultValue] is returned.
     *
     * Shared preferences are encrypted. Do not create your own instance to add or retrieve data.
     * Instead, call operations of this controller.
     *
     * @param key          Key to get corresponding value.
     * @param defaultValue Default value to return if given [key] does not exist in prefs or if
     * key value is invalid.
     */
    override fun getBool(key: String, defaultValue: Boolean): Boolean {
        return getSharedPrefs().getBoolean(key, defaultValue)
    }

}

/**
 * Assisting class containing pref keys. Instead of creating keys of strings, you can just set or
 * get values from declared keys here. This class will inject the implementation of
 * [PrefsController] to manipulate the data storing process.
 *
 * Setting a value to a key belonging to this class will automatically store that value with the
 * corresponding key to device prefs. Getting a value on the other hand will retrieve the storage
 * prefs value of that key.
 *
 * Setting a `null` _(where applicable)_ value to a key will remove that keys from storage prefs.
 */
@Singleton
class PrefKeys @Inject constructor(
    private val prefsController: PrefsController
) {

    /**
     * Defines that user has requested to reset his password and though, when the app starts, user
     * should be navigated to a screen prompting user to change the default password that was set.
     */
    var hasResetPassword: Boolean
        get() {
            return prefsController.getBool("hasResetPassword", false)
        }
        set(value) {
            if (!value) prefsController.clear("hasResetPassword")
            else prefsController.setBool("hasResetPassword", value)
        }

    /**
     * String json that represents the stored auth data.
     */
    var authData: String
        get() {
            return prefsController.getString("authData", "")
        }
        set(value) {
            if (value.isEmpty()) prefsController.clear("authData")
            else prefsController.setString("authData", value)
        }


}