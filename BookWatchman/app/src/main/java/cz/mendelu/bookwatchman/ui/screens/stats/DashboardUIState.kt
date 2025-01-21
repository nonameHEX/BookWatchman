package cz.mendelu.bookwatchman.ui.screens.stats

import cz.mendelu.bookwatchman.database.Book
import java.io.Serializable

sealed class DashboardUIState : Serializable {
    object Loading : DashboardUIState()
    class DataLoaded(val books: List<Book>) : DashboardUIState()
}