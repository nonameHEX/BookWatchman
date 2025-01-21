package cz.mendelu.bookwatchman.ui.screens.search_online

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.mendelu.bookwatchman.R
import cz.mendelu.bookwatchman.communication.CommunicationResult
import cz.mendelu.bookwatchman.communication.IBooksRemoteRepository
import cz.mendelu.bookwatchman.model.SearchBarOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@HiltViewModel
class SearchOnlineViewModel @Inject constructor(private val repository: IBooksRemoteRepository) : ViewModel() {
    private val _uiState: MutableStateFlow<SearchOnlineUIState> = MutableStateFlow(value = SearchOnlineUIState.Loading)
    val uiState: StateFlow<SearchOnlineUIState> get() = _uiState.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _searchOption = MutableStateFlow(SearchBarOption.TITLE)
    val searchOption = _searchOption.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    init {
        Log.d("SearchOnlineViewModel", "init")
        _uiState.update {
            SearchOnlineUIState.ReadyForSearch
        }
    }

    fun onSearchTextChanged(newText: String) {
        Log.d("SearchOnlineViewModel", "onSearchTextChanged: $newText")
        _searchText.value = newText
        if (newText.isBlank()) {
            _uiState.update { SearchOnlineUIState.ReadyForSearch }
        }
    }

    fun onChangeSearchOption(option: SearchBarOption) {
        Log.d("SearchOnlineViewModel", "onChangeSearchOption: ${option.value}")
        _searchOption.value = option
        onSearchTextChanged("")
    }

    private fun formatQuery(query: String): String {
        val prefix = searchOption.value.toQueryPrefix()
        val encodedQuery = URLEncoder.encode(query.trim(), StandardCharsets.UTF_8.toString())

        return "$prefix$encodedQuery"
    }

    private fun searchBooks(query: String) {
        if (query.isBlank()) {
            _uiState.update { SearchOnlineUIState.ReadyForSearch }
            return
        }
        _isSearching.value = true

        viewModelScope.launch {
            val formattedQuery = formatQuery(query)
            val result = withContext(Dispatchers.IO) {
                Log.d("SearchOnlineViewModel", "Search query: $formattedQuery")
                repository.searchBooks(formattedQuery)
            }
            when(result){
                is CommunicationResult.Success -> {
                    _uiState.update {
                        Log.d("SearchOnlineViewModel", "Success books found: ${result.data.items.count()}")
                        _isSearching.value = false
                        SearchOnlineUIState.DataLoaded(result.data)
                    }
                }
                is CommunicationResult.ConnectionError -> {
                    _uiState.update {
                        _isSearching.value = false
                        SearchOnlineUIState.Error(SearchOnlineError(R.string.no_internet_connection))
                    }
                }
                is CommunicationResult.Error -> {
                    if (result.error.message == "Empty response body") {
                        _uiState.update {
                            _isSearching.value = false
                            SearchOnlineUIState.Error(SearchOnlineError(R.string.empty_response_body))
                        }
                    } else {
                        _uiState.update {
                            _isSearching.value = false
                            SearchOnlineUIState.Error(SearchOnlineError(R.string.generic_error))
                        }
                    }
                }
                is CommunicationResult.Exception -> {
                    _uiState.update {
                        _isSearching.value = false
                        SearchOnlineUIState.Error(SearchOnlineError(R.string.generic_error))
                    }
                }
            }
        }
    }

    @OptIn(FlowPreview::class)
    fun startDebouncedSearch() {
        Log.d("SearchOnlineViewModel", "startDebouncedSearch")
        viewModelScope.launch {
            _searchText
                .debounce(750)
                .collect { query ->
                    if (query.isNotBlank()) {
                        searchBooks(query)
                    }
                }
        }
    }
}