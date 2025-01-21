package cz.mendelu.bookwatchman.ui.screens.stats

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.mendelu.bookwatchman.database.IBookRepository
import cz.mendelu.bookwatchman.datastore.IDataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val databaseRepository: IBookRepository,
    private val datastoreRepository: IDataStoreRepository
) : ViewModel() {
    private val _uiState: MutableStateFlow<DashboardUIState> = MutableStateFlow(value = DashboardUIState.Loading)
    val uiState: StateFlow<DashboardUIState> get() = _uiState.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName = _userName.asStateFlow()

    init {
        Log.d("DashboardViewModel", "init")
        viewModelScope.launch {
            databaseRepository.getAll().collect { books ->
                _uiState.update {
                    DashboardUIState.DataLoaded(books)
                }
            }
        }
    }

    fun loadUsername() {
        viewModelScope.launch {
            datastoreRepository.userNameFlow.collect() {
                _userName.value = it
            }
        }
    }
}