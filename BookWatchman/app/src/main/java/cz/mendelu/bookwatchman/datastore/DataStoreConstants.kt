package cz.mendelu.bookwatchman.datastore

import androidx.datastore.preferences.core.stringPreferencesKey

object DataStoreConstants {
    val USER_NAME = stringPreferencesKey("user_name")
    val FILTER_SORT_ORDER = stringPreferencesKey("filter_sort_order")
}