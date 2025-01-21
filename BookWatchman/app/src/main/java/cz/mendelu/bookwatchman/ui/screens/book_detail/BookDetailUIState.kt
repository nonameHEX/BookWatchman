package cz.mendelu.bookwatchman.ui.screens.book_detail

import cz.mendelu.bookwatchman.database.Book
import java.io.Serializable

sealed class BookDetailUIState : Serializable {
    object Loading : BookDetailUIState()
    class Changed(val book: Book) : BookDetailUIState()
    class DataLoaded(val book: Book) : BookDetailUIState()
}