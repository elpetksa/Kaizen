package com.elpet.kaizen.util.extensions

import android.text.format.DateUtils
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import java.util.*

// ┌───────────────────────────────────────────────────────────────────────────────────────────┐
//   → CONVERSION FUNCTIONS
// └───────────────────────────────────────────────────────────────────────────────────────────┘

/**
 * Converts ISO date string to a [ZonedDateTime] object. This means that date string can contain
 * offset information. Given string must gave the [style] provider in order to be parsed
 * successfully.
 *
 * This is a `null` and exception safe operation. This means that if this string is an
 * empty string or not a valid date string, [ZonedDateTime] of `0` timestamp time is returned.
 *
 * Notice that this requires a date time zoned string. To parse a single date string, consider
 * using [toLocalDateTime].
 *
 * @param style      Date time format of this string to parse and format.
 * @param truncateAt Formatter used to format this date to.
 *
 * @return A [ZonedDateTime] representing this date string or an object of `0` timestamp time if
 * parsing failed.
 */
fun String.toDateTime(style: DateTimeFormatter,
                      truncateAt: ChronoUnit = ChronoUnit.SECONDS): ZonedDateTime {
    return try {
        ZonedDateTime
                .parse(this, style)
                .truncatedTo(truncateAt)
    } catch (cause: Throwable) {
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault())
    }
}

/**
 * Converts ISO date string to a [LocalDate] object. This means that date string can contain
 * offset information. Given string must gave the [style] provider in order to be parsed
 * successfully.
 *
 * This is a `null` and exception safe operation. This means that if this string is an
 * empty string or not a valid date string, [ZonedDateTime] of `0` timestamp time is returned.
 *
 * To parse a zoned date time string, consider using [toDateTime].
 *
 * @param style Date time format of this string to parse and format.
 *
 * @return A [ZonedDateTime] representing this date string or an object of `0` timestamp time if
 * parsing failed.
 */
fun String.toLocalDateTime(style: DateTimeFormatter): LocalDate {
    return try {
        LocalDate
            .parse(this, style)
    } catch (cause: Throwable) {
        LocalDate.now()
    }
}

// ┌───────────────────────────────────────────────────────────────────────────────────────────┐
//   → STRING FUNCTIONS
// └───────────────────────────────────────────────────────────────────────────────────────────┘

/**
 * Converts ISO date string to a string using given [formatter]. Given string must have the [style]
 * provided in order to be parsed successfully. Parsing uses current locale as set in
 * [JPBApplication.currentLocale] in order to localize returned string and format it
 * properly. You do not need to set locale or zone to given [formatter].
 *
 * This is a `null` and exception safe operation. This means that if this string is an
 * empty string or not a valid date string, this string is returned.
 *
 * @param style      Date time format of this string to parse and format.
 * @param formatter  Formatter used to format this date to.
 * @param truncateAt [ChronoUnit] to truncate returned string at.
 *
 * @return A localized string formatted with given formatter representing this ISO date. Empty
 * string if this string is `null` or empty or not in valid date format.
 */
fun String.dateToString(style: DateTimeFormatter,
                         formatter: DateTimeFormatter,
                         truncateAt: ChronoUnit = ChronoUnit.SECONDS): String {
    return try {
        this.toDateTime(style, truncateAt)
            .toString(formatter)
    } catch (cause: Throwable) {
        this
    }
}

// ┌───────────────────────────────────────────────────────────────────────────────────────────┐
//   → LOCAL DATE FUNCTIONS
// └───────────────────────────────────────────────────────────────────────────────────────────┘

/**
 * Formats this [LocalDate] using the given [formatter]. Parsing uses [Locale.ENGLISH] in
 * order to localize returned string and format it
 * properly. You do not need to set locale or zone to given [formatter].
 *
 * @param formatter Formatter used to format this date to.
 *
 * @return  A localized string formatted with given formatter representing this ISO date.
 */
fun LocalDate.toString(formatter: DateTimeFormatter): String {
    return this.format(formatter
        .withZone(ZoneId.systemDefault())
        .withLocale(Locale.ENGLISH))
}

fun LocalDate.toMillis(): Long {
    return this.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

// ┌───────────────────────────────────────────────────────────────────────────────────────────┐
//   → ZONED DATETIME FUNCTIONS
// └───────────────────────────────────────────────────────────────────────────────────────────┘

/**
 * Formats this [ZonedDateTime] using the given [formatter]. Parsing uses [Locale.ENGLISH] in
 * order to localize returned string and format it
 * properly. You do not need to set locale or zone to given [formatter].
 *
 * @param formatter Formatter used to format this date to.
 *
 * @return  A localized string formatted with given formatter representing this ISO date.
 */
fun ZonedDateTime.toString(formatter: DateTimeFormatter): String {
    return this.format(formatter
            .withZone(ZoneId.systemDefault())
            .withLocale(Locale.ENGLISH))
}

/**
 * Converts [ZonedDateTime] object to time localized string. This is actually a short call of
 * [toString] using [DateTimeFormatter.ofLocalizedTime] with [FormatStyle.SHORT] style.
 *
 * @return Localized string of this [ZonedDateTime] object.
 */
fun ZonedDateTime.toShortTime(): String {
    return this.toString(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
}

/**
 * Converts [ZonedDateTime] object to time localized string. This is actually a short call of
 * [toString] using [DateTimeFormatter.ofLocalizedTime] with [FormatStyle.MEDIUM] style.
 *
 * @return Localized string of this [ZonedDateTime] object.
 */
fun ZonedDateTime.toMediumTime(): String {
    return this.toString(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))
}

/**
 * Converts [ZonedDateTime] object to date localized string. This is actually a short call of
 * [toString] using [DateTimeFormatter.ofLocalizedDate] with [FormatStyle.SHORT] style.
 *
 * @return Localized string of this [ZonedDateTime] object.
 */
fun ZonedDateTime.toShortDate(): String {
    return this.toString(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
}

/**
 * Converts [ZonedDateTime] object to date localized string. This is actually a short call of
 * [toString] using [DateTimeFormatter.ofLocalizedDate] with [FormatStyle.MEDIUM] style.
 *
 * @return Localized string of this [ZonedDateTime] object.
 */
fun ZonedDateTime.toMediumDate(): String {
    return this.toString(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
}

/**
 * Converts [ZonedDateTime] object to date & time localized string. This is actually a short call of
 * [toString] using [DateTimeFormatter.ofLocalizedDateTime] with [FormatStyle.SHORT] style.
 *
 * @return Localized string of this [ZonedDateTime] object.
 */
fun ZonedDateTime.toShortDateTime(): String {
    return this.toString(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
}

/**
 * Converts [ZonedDateTime] object to date & time localized string. This is actually a short call of
 * [toString] using [DateTimeFormatter.ofLocalizedDateTime] with [FormatStyle.MEDIUM] style.
 *
 * @return Localized string of this [ZonedDateTime] object.
 */
fun ZonedDateTime.toMediumDateTime(): String {
    return this.toString(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))
}

// ┌───────────────────────────────────────────────────────────────────────────────────────────┐
//   → UTIL FUNCTIONS
// └───────────────────────────────────────────────────────────────────────────────────────────┘

/**
 * Converts ISO date string to timestamp in milliseconds. Given string must have the [style]
 * provided in order to be parsed successfully.
 *
 * This is a `null` and exception safe operation. This means that if this string is `null`, an
 * empty string or not a valid date string, `0` will be returned.
 *
 * @param style Date time format of this string to parse and format.
 *
 * @return This date as timestamp in milliseconds. `0` if parsing failed.
 */
fun String.toMillis(style: DateTimeFormatter): Long {
    return this.toDateTime(style).toInstant().toEpochMilli()
}

/**
 * Converts this timestamp in millis to a [ZonedDateTime].
 *
 * @return A [ZonedDateTime] converted from this timestamp.
 */
fun Long.toZonedDateTime(): ZonedDateTime {
    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
}

/**
 * Defines if this [ZonedDateTime] represents this day. This means that the date of this object and
 * [LocalDate.now] are equal.
 *
 * @return `true` if this [ZonedDateTime] is today. `false` otherwise.
 */
fun ZonedDateTime.isToday(): Boolean {
    return DateUtils.isToday(this.toInstant().toEpochMilli())
}

/**
 * Defines if this [ZonedDateTime] represents the day after today. This means that the date of this
 * object and [LocalDate.plusDays] +1 are equal.
 *
 * @return `true` if this [ZonedDateTime] is tomorrow. `false` otherwise.
 */
fun ZonedDateTime.isTomorrow(): Boolean {
    return DateUtils.isToday(this.toInstant().toEpochMilli() - DateUtils.DAY_IN_MILLIS)
}

/**
 * Defines how many units the period between now and this [ZonedDateTime] object has. For example,
 * if you want to get the seconds between this [ZonedDateTime] object and now object, you can
 * invoke this function with a unit of [ChronoUnit.SECONDS].
 *
 * @param unit Unit to get the difference between this [ZonedDateTime] and now.
 *
 * @return Difference in given [unit] between this [ZonedDateTime] and now.
 */
fun ZonedDateTime.fromNow(unit: ChronoUnit): Long {
    return ZonedDateTime.now().until(this, unit)
}

/**
 * Defines how many units the period between now and this [LocalDate] object has. For example,
 * if you want to get the seconds between this [LocalDate] object and now object, you can
 * invoke this function with a unit of [ChronoUnit.SECONDS].
 *
 * @param unit Unit to get the difference between this [LocalDate] and now.
 *
 * @return Difference in given [unit] between this [LocalDate] and now.
 */
fun LocalDate.fromNow(unit: ChronoUnit): Long {
    return LocalDate.now().until(this, unit)
}