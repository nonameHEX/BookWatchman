package cz.mendelu.bookwatchman.model

import android.content.Context
import cz.mendelu.bookwatchman.R

enum class BookSortOrderOption(val value: Int) {
    DB_ID_ASCENDING(0),
    DB_ID_DESCENDING(1),
    ALFA_ASCENDING(2),
    ALFA_DESCENDING(3);


    fun getDisplayName(context: Context): String {
        return when (this) {
            DB_ID_ASCENDING -> context.getString(R.string.id_ascending)
            DB_ID_DESCENDING -> context.getString(R.string.id_descending)
            ALFA_ASCENDING -> context.getString(R.string.name_ascending)
            ALFA_DESCENDING -> context.getString(R.string.name_descending)
        }
    }

    companion object {
        fun fromInt(value: Int): SearchBarOption {
            return SearchBarOption.entries.first { it.value == value }
        }
    }
}