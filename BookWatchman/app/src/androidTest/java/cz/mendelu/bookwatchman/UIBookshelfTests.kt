package cz.mendelu.bookwatchman

import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import cz.mendelu.bookwatchman.mock.DatabaseMock
import cz.mendelu.bookwatchman.navigation.Destination
import cz.mendelu.bookwatchman.navigation.NavGraph
import cz.mendelu.bookwatchman.ui.activity.MainActivity
import cz.mendelu.bookwatchman.ui.screens.main_menu_bookshelf.TestTagMainMenuBookshelfListOfBooksLazyList
import cz.mendelu.bookwatchman.ui.screens.main_menu_bookshelf.TestTagMainMenuScreenAddButton
import cz.mendelu.bookwatchman.ui.screens.stats.TestTagStatsScreenButton
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters

@ExperimentalCoroutinesApi
@HiltAndroidTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class UIBookshelfTests {
    private lateinit var navController: NavHostController

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    // 1. Test pro navigaci z Dashboard do Main Menu
    @Test
    fun test_navigation_to_main_menu() {
        launchDashboardScreenWithNavigation()

        with(composeRule) {
            onNodeWithTag(TestTagStatsScreenButton).assertExists()
            onNodeWithTag(TestTagStatsScreenButton).assertIsDisplayed()
            onNodeWithTag(TestTagStatsScreenButton).performClick()
            mainClock.advanceTimeBy(1000)
            waitForIdle()
            val route = navController.currentBackStackEntry?.destination?.route
            assertTrue(route == Destination.MainMenuScreen.route)
            Thread.sleep(1000)
        }
    }

    // 2. Test zda existují knihy v knihovně
    @Test
    fun test_display_books_in_bookshelf_exists() {
        launchBookshelfScreenWithNavigation()

        with(composeRule) {
            onNodeWithTag(TestTagMainMenuBookshelfListOfBooksLazyList).assertExists()
            onNodeWithTag(TestTagMainMenuBookshelfListOfBooksLazyList).assertIsDisplayed()

            Thread.sleep(1000)
        }
    }

    // 3. Test funkčnosti klikatelnosti na detail knihy
    @Test
    fun test_navigate_to_correct_book_from_bookshelf() {
        launchBookshelfScreenWithNavigation()

        with(composeRule) {
            val targetBook = DatabaseMock.book1
            assert(targetBook.id != null)
            onNodeWithTag(TestTagMainMenuBookshelfListOfBooksLazyList).assertIsDisplayed()

            onNode(hasText(targetBook.title)).assertIsDisplayed()
            onNode(hasText(targetBook.title)).performClick()
            waitForIdle()
            val route = navController.currentBackStackEntry?.destination?.route
            Assert.assertTrue(route?.contains(Destination.BookDetailsScreen.route) ?: false)
            Thread.sleep(1000)
        }
    }

    // 4. Test pro zobrazení výběru přidání knihy do knihovny
    @Test
    fun test_navigate_to_choose_add_option_screen() {
        launchBookshelfScreenWithNavigation()

        with(composeRule) {
            onNodeWithTag(TestTagMainMenuScreenAddButton).assertExists()
            onNodeWithTag(TestTagMainMenuScreenAddButton).performClick()
            composeRule.mainClock.advanceTimeBy(1000)
            waitForIdle()
            val route = navController.currentBackStackEntry?.destination?.route
            assertTrue(route == Destination.ChooseAddOptionScreen.route)
            Thread.sleep(1000)
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    private fun launchDashboardScreenWithNavigation() {
        composeRule.activity.setContent {
            MaterialTheme {
                navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    startDestination = Destination.StatsScreen.route
                )
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    private fun launchBookshelfScreenWithNavigation() {
        composeRule.activity.setContent {
            MaterialTheme {
                navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    startDestination = Destination.MainMenuScreen.route
                )
            }
        }
    }
}