package cz.mendelu.bookwatchman.ui.screens.search_scan

import cz.mendelu.bookwatchman.communication.model.BookResponse
import java.io.Serializable

sealed class SearchScanUIState : Serializable {
    object Loading : SearchScanUIState()
    object ReadyForSearch: SearchScanUIState()
    class DataLoaded(val books: BookResponse) : SearchScanUIState()
    class Error(val error: SearchScanError) : SearchScanUIState()
}