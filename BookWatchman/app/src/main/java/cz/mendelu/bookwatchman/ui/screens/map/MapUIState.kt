package cz.mendelu.bookwatchman.ui.screens.map

import java.io.Serializable

sealed class MapUIState: Serializable {
    object Loading : MapUIState()
    object DataLoaded : MapUIState()
    class Error(val error: MapError): MapUIState()
}