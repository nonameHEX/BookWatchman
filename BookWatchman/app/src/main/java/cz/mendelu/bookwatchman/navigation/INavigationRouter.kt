package cz.mendelu.bookwatchman.navigation

import androidx.navigation.NavController

interface INavigationRouter {
    fun getNavController(): NavController
    fun returnBack()

    fun navigateToStatsScreen()
    fun navigateToMainMenuScreen()
    fun navigateToChooseAddOptionScreen()
    fun navigateToSearchOnlineScreen()
    fun navigateToSearchScanScreen()
    fun navigateToAddBookScreen(id: String?)
    fun navigateToBookDetailScreen(id: Long?)
    fun navigateToMapScreen()
    fun navigateToAppSettingsScreen()
}