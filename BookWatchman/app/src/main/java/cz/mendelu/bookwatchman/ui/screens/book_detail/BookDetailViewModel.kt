package cz.mendelu.bookwatchman.ui.screens.book_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.mendelu.bookwatchman.database.Book
import cz.mendelu.bookwatchman.database.BookState
import cz.mendelu.bookwatchman.database.IBookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(private val repository: IBookRepository) : ViewModel() {
    private val _uiState: MutableStateFlow<BookDetailUIState> = MutableStateFlow(value = BookDetailUIState.Loading)
    val uiState: StateFlow<BookDetailUIState> get() = _uiState.asStateFlow()

    private var _isChanged = MutableStateFlow(false)
    val isChanged = _isChanged.asStateFlow()

    fun loadBook(bookId: Long?) {
        viewModelScope.launch {
            val book = repository.getBookById(bookId!!)
            _uiState.update {
                BookDetailUIState.DataLoaded(book)
            }
        }
    }

    fun onStateChange(newState: BookState, book: Book) {
        if (book.bookState != newState) {
            book.state = newState.value
            _isChanged.value = true
        }

        _uiState.update {
            BookDetailUIState.Changed(book)
        }
    }

    fun onPagesReadChange(newPagesRead: Int, book: Book) {
        if (book.pagesRead != newPagesRead) {
            book.pagesRead = newPagesRead
            _isChanged.value = true
        }

        _uiState.update {
            BookDetailUIState.Changed(book)
        }
    }

    fun saveBook(book: Book) {
        viewModelScope.launch {
            if (book.bookState != BookState.READING) {
                book.pagesRead = 0
            }
            repository.update(book)
            _isChanged.value = false
        }
    }

    fun deleteBook(book: Book) {
        viewModelScope.launch {
            repository.delete(book)
        }
    }
}