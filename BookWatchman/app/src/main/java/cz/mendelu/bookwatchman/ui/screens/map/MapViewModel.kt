package cz.mendelu.bookwatchman.ui.screens.map

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {
    private val _uiState: MutableStateFlow<MapUIState> = MutableStateFlow(value = MapUIState.Loading)
    val uiState: StateFlow<MapUIState> get() = _uiState.asStateFlow()

    init {
        Log.d("MapViewModel", "init")
    }

    fun updateToLoaded(){
        _uiState.update {
            MapUIState.DataLoaded
        }
    }
}