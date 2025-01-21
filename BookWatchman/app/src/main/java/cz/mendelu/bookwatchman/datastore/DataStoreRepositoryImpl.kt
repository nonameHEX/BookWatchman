package cz.mendelu.bookwatchman.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import cz.mendelu.bookwatchman.model.BookSortOrderOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreRepositoryImpl(
    private val context: Context
): IDataStoreRepository {

    private val dataStore: DataStore<Preferences> = context.dataStore

    override val userNameFlow: Flow<String>
        get() = dataStore.data.map { preferences ->
            preferences[DataStoreConstants.USER_NAME] ?: ""
        }

    override val filterSortOrderFlow: Flow<BookSortOrderOption>
        get() = dataStore.data.map { preferences ->
            val value = preferences[DataStoreConstants.FILTER_SORT_ORDER]
                ?: BookSortOrderOption.DB_ID_ASCENDING.name
            BookSortOrderOption.valueOf(value)
        }

    override suspend fun saveUserName(userName: String) {
        dataStore.edit { preferences ->
            preferences[DataStoreConstants.USER_NAME] = userName
        }
    }

    override suspend fun saveFilterSortOrder(sortOrder: BookSortOrderOption) {
        dataStore.edit { preferences ->
            preferences[DataStoreConstants.FILTER_SORT_ORDER] = sortOrder.name
        }
    }
}