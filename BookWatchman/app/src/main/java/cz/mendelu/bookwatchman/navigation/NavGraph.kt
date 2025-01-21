package cz.mendelu.bookwatchman.navigation

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cz.mendelu.bookwatchman.ui.screens.add_book.AddBookScreen
import cz.mendelu.bookwatchman.ui.screens.app_settings.AppSettingsScreen
import cz.mendelu.bookwatchman.ui.screens.book_detail.BookDetailsScreen
import cz.mendelu.bookwatchman.ui.screens.choose_add_option.ChooseAddOptionScreen
import cz.mendelu.bookwatchman.ui.screens.main_menu_bookshelf.MainMenuBookshelfScreen
import cz.mendelu.bookwatchman.ui.screens.map.MapScreen
import cz.mendelu.bookwatchman.ui.screens.search_online.SearchOnlineScreen
import cz.mendelu.bookwatchman.ui.screens.search_scan.SearchScanScreen
import cz.mendelu.bookwatchman.ui.screens.stats.StatsScreen

@ExperimentalFoundationApi
@Composable

fun NavGraph(
    navController: NavHostController = rememberNavController(),
    navigation: INavigationRouter = remember { NavigationRouterImpl(navController) },
    startDestination: String
){
    NavHost(
        navController = navController,
        startDestination = startDestination){
        Log.d("NavGGHost", "StartDestination: $startDestination")
        composable(Destination.StatsScreen.route){
            StatsScreen(navigation = navigation)
        }

        composable(Destination.MainMenuScreen.route){
            MainMenuBookshelfScreen(navigation = navigation)
        }

        composable(Destination.ChooseAddOptionScreen.route){
            ChooseAddOptionScreen(navigation = navigation)
        }

        composable(Destination.SearchOnlineScreen.route){
            SearchOnlineScreen(navigation = navigation)
        }

        composable(Destination.SearchScanScreen.route){
            SearchScanScreen(navigation = navigation)
        }

        composable(
            route = Destination.AddBookScreen.route + "/{id}",
            arguments = listOf(
                navArgument("id"){
                    type = NavType.StringType
                    nullable = true
                    defaultValue = "_"
                }
            )
        ) {
            val id = it.arguments?.getString("id")
            AddBookScreen(navigation = navigation, bookId = if (id != "_") id else null)
        }

        composable(
            route = Destination.BookDetailsScreen.route + "/{id}",
            arguments = listOf(
                navArgument("id"){
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ){
            val id = it.arguments?.getLong("id")
            Log.d("NavGGDetail", "Book ID: $id")
            BookDetailsScreen(navigation = navigation, bookId = if (id != -1L) id else null)
        }

        composable(Destination.MapScreen.route){
            MapScreen(navigation = navigation)
        }

        composable(Destination.AppSettingsScreen.route){
            AppSettingsScreen(navigation = navigation)
        }
    }
}
