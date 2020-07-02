package com.viveksharma.firebaseblog.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Truncate long text with a preference for word boundaries and without trailing punctuation.
 */
private val PUNCTUATION = listOf(", ", "; ", ": ", " ")
fun String.smartTruncate(length: Int): String {
    val words = split(" ")
    var added = 0
    var hasMore = false
    val builder = StringBuilder()
    for (word in words) {
        if (builder.length > length) {
            hasMore = true
            break
        }
        builder.append(word)
        builder.append(" ")
        added += 1
    }

    PUNCTUATION.map {
        if (builder.endsWith(it)) {
            builder.replace(builder.length - it.length, builder.length, "")
        }
    }

    if (hasMore) {
        builder.append("...")
    }
    return builder.toString()
}

/**
 * Convert Timestamp to a date and time format
 */
fun convertedDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("d MMM yyyy, h:mm a")
    return formatter.format(Date(timestamp))
}