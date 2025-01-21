package cz.mendelu.bookwatchman.navigation

sealed class Destination(
    val route: String
){
    object StatsScreen : Destination(route = "stats")
    object MainMenuScreen : Destination(route = "main_menu")
    object ChooseAddOptionScreen : Destination(route = "choose_add_option")
    object SearchOnlineScreen : Destination(route = "search_online")
    object SearchScanScreen : Destination(route = "search_scan")
    object AddBookScreen : Destination(route = "add_book")
    object BookDetailsScreen : Destination(route = "book_details")
    object MapScreen : Destination(route = "map")
    object AppSettingsScreen : Destination(route = "app_settings")
}

