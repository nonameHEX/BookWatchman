package cz.mendelu.bookwatchman.ui.screens.app_settings

import java.io.Serializable

sealed class AppSettingsUIState : Serializable {
    object Loading : AppSettingsUIState()
    object DataLoaded : AppSettingsUIState()
    object SettingsChanged : AppSettingsUIState()
}