package cz.mendelu.bookwatchman.ui.screens.add_book

import cz.mendelu.bookwatchman.communication.model.BookItem
import java.io.Serializable

sealed class AddBookUIState () : Serializable {
    object Default : AddBookUIState()
    object Loading : AddBookUIState()
    object ManualAdd : AddBookUIState()
    object Changed : AddBookUIState()
    class DataLoaded(val book: BookItem) : AddBookUIState()
    class Error(val error: AddBookError) : AddBookUIState()
}
