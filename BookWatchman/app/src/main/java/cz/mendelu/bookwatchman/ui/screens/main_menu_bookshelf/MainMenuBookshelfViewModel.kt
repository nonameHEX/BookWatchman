package cz.mendelu.bookwatchman.ui.screens.main_menu_bookshelf

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.mendelu.bookwatchman.database.Book
import cz.mendelu.bookwatchman.database.IBookRepository
import cz.mendelu.bookwatchman.datastore.IDataStoreRepository
import cz.mendelu.bookwatchman.model.BookSortOrderOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainMenuBookshelfViewModel @Inject constructor(
    private val databaseRepository: IBookRepository,
    private val datastoreRepository: IDataStoreRepository
) : ViewModel() {
    private val _uiState: MutableStateFlow<MainMenuBookshelfUIState> = MutableStateFlow(value = MainMenuBookshelfUIState.Loading)
    val uiState: StateFlow<MainMenuBookshelfUIState> get() = _uiState.asStateFlow()

    private var sortOrder: BookSortOrderOption = BookSortOrderOption.ALFA_ASCENDING

    init {
        Log.d("MainMenuBookshelfViewModel", "init")
        viewModelScope.launch {
            datastoreRepository.filterSortOrderFlow.collect {
                sortOrder = it
            }
        }
    }

    fun loadBooks() {
        Log.d("MainMenuBookshelfViewModel", "sort order: $sortOrder")
        viewModelScope.launch {
            databaseRepository.getAll().collect { books ->
                val sortedBooks = applySortOrder(books)
                _uiState.update {
                    MainMenuBookshelfUIState.DataLoaded(sortedBooks)
                }
            }
        }
    }

    private fun applySortOrder(books: List<Book>): List<Book> {
        return when (sortOrder) {
            BookSortOrderOption.DB_ID_ASCENDING -> books.sortedBy { it.id }
            BookSortOrderOption.DB_ID_DESCENDING -> books.sortedByDescending { it.id }
            BookSortOrderOption.ALFA_ASCENDING -> books.sortedBy { it.title }
            BookSortOrderOption.ALFA_DESCENDING -> books.sortedByDescending { it.title }
        }
    }
}