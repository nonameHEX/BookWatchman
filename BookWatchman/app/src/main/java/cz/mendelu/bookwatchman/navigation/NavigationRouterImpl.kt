package cz.mendelu.bookwatchman.navigation

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController

class NavigationRouterImpl(private val navController: NavController) : INavigationRouter {
    override fun getNavController(): NavController = navController
    override fun returnBack() {
        navController.popBackStack()
    }

    override fun navigateToStatsScreen() {
        if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
            navController.navigate(Destination.StatsScreen.route)
        }
    }

    override fun navigateToMainMenuScreen() {
        if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
            navController.navigate(Destination.MainMenuScreen.route)
        }
    }

    override fun navigateToChooseAddOptionScreen() {
        if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
            navController.navigate(Destination.ChooseAddOptionScreen.route)
        }
    }

    override fun navigateToSearchOnlineScreen() {
        if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
            navController.navigate(Destination.SearchOnlineScreen.route)
        }
    }

    override fun navigateToSearchScanScreen() {
        if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
            navController.navigate(Destination.SearchScanScreen.route)
        }
    }

    override fun navigateToAddBookScreen(id: String?) {
        if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
            navController.navigate("${Destination.AddBookScreen.route}/${id}")
        }
    }

    override fun navigateToBookDetailScreen(id: Long?) {
        if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
            navController.navigate("${Destination.BookDetailsScreen.route}/${id}")
        }
    }

    override fun navigateToMapScreen() {
        if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
            navController.navigate(Destination.MapScreen.route)
        }
    }

    override fun navigateToAppSettingsScreen() {
        if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
            navController.navigate(Destination.AppSettingsScreen.route)
        }
    }
}