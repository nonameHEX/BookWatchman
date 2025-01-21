package cz.mendelu.bookwatchman.ui.screens.search_online

import cz.mendelu.bookwatchman.communication.model.BookResponse
import java.io.Serializable

sealed class SearchOnlineUIState : Serializable {
    object Loading : SearchOnlineUIState()
    object ReadyForSearch: SearchOnlineUIState()
    class DataLoaded(val books: BookResponse) : SearchOnlineUIState()
    class Error(val error: SearchOnlineError) : SearchOnlineUIState()
}