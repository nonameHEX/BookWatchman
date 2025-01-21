package cz.mendelu.bookwatchman.model

import android.content.Context
import cz.mendelu.bookwatchman.R

enum class SearchBarOption(val value: Int) {
    TITLE(0),
    AUTHOR(1);

    fun getDisplayName(context: Context): String {
        return when (this) {
            TITLE -> context.getString(R.string.title)
            AUTHOR -> context.getString(R.string.author)
        }
    }

    fun toQueryPrefix(): String {
        return when (this) {
            TITLE -> "intitle:"
            AUTHOR -> "inauthor:"
        }
    }

    companion object {
        fun fromInt(value: Int): SearchBarOption {
            return SearchBarOption.entries.first { it.value == value }
        }
    }
}