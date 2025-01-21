package cz.mendelu.bookwatchman.datastore

import cz.mendelu.bookwatchman.model.BookSortOrderOption
import kotlinx.coroutines.flow.Flow

interface IDataStoreRepository {
    val userNameFlow: Flow<String>
    val filterSortOrderFlow: Flow<BookSortOrderOption>
    suspend fun saveUserName(userName: String)
    suspend fun saveFilterSortOrder(sortOrder: BookSortOrderOption)
}