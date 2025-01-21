package cz.mendelu.bookwatchman.ui.screens.app_settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.mendelu.bookwatchman.database.IBookRepository
import cz.mendelu.bookwatchman.datastore.IDataStoreRepository
import cz.mendelu.bookwatchman.model.BookSortOrderOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppSettingsViewModel @Inject constructor(
    private val datastoreRepository: IDataStoreRepository,
    private val databaseRepository: IBookRepository
): ViewModel() {
    private val _uiState: MutableStateFlow<AppSettingsUIState> = MutableStateFlow(value = AppSettingsUIState.Loading)
    val uiState: StateFlow<AppSettingsUIState> get() = _uiState.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName = _userName.asStateFlow()

    private val _sortOrder = MutableStateFlow(BookSortOrderOption.DB_ID_ASCENDING)
    val sortOrder = _sortOrder.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                datastoreRepository.userNameFlow,
                datastoreRepository.filterSortOrderFlow
            ) { name, sortOrder ->
                Pair(name, sortOrder)
            }.collect { (name, sortOrder) ->
                _userName.value = name
                _sortOrder.value = sortOrder
                _uiState.update {
                    AppSettingsUIState.DataLoaded
                }
            }
        }
    }

    fun deleteAllBooks(){
        viewModelScope.launch {
            databaseRepository.deleteAll()
            _uiState.update {
                AppSettingsUIState.SettingsChanged
            }
        }
    }

    fun onSortOrderChanged(newSortOrder: BookSortOrderOption) {
        _sortOrder.value = newSortOrder
        viewModelScope.launch {
            datastoreRepository.saveFilterSortOrder(newSortOrder)
            _uiState.update {
                AppSettingsUIState.SettingsChanged
            }
        }
    }

    fun onUserNameChanged(newText: String) {
        _userName.value = newText
        _uiState.update {
            AppSettingsUIState.SettingsChanged
        }
    }

    private fun saveUserName(userName: String){
        viewModelScope.launch {
            datastoreRepository.saveUserName(userName)
            _uiState.update {
                AppSettingsUIState.SettingsChanged
            }
        }
    }

    @OptIn(FlowPreview::class)
    fun startDebouncedSaveUsername() {
        viewModelScope.launch {
            _userName
                .debounce(750)
                .collect { name ->
                    saveUserName(name)
                }
        }
    }
}