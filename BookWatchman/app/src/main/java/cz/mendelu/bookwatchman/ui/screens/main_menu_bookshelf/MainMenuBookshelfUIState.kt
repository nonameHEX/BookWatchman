package cz.mendelu.bookwatchman.ui.screens.main_menu_bookshelf

import cz.mendelu.bookwatchman.database.Book
import java.io.Serializable

sealed class MainMenuBookshelfUIState : Serializable {
    object Loading : MainMenuBookshelfUIState()
    class DataLoaded(val books: List<Book>) : MainMenuBookshelfUIState()
}