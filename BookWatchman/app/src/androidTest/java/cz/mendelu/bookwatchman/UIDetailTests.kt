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
import cz.mendelu.bookwatchman.ui.screens.book_detail.TestTagDeleteButton
import cz.mendelu.bookwatchman.ui.screens.main_menu_bookshelf.TestTagMainMenuBookshelfListOfBooksLazyList
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
class UIDetailTests {
    private lateinit var navController: NavHostController

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    // 1. Test pro zobrazení detailu knihy z knihovny
    @Test
    fun test_book_detail_screen_displays_correct_book() {
        launchBookshelfScreenWithNavigation()
        val targetBook = DatabaseMock.book1

        with(composeRule){
            assert(targetBook.id != null)
            onNodeWithTag(TestTagMainMenuBookshelfListOfBooksLazyList).assertIsDisplayed()

            onNode(hasText(targetBook.title)).assertIsDisplayed()
            onNode(hasText(targetBook.title)).performClick()
            mainClock.advanceTimeBy(1000)
            waitForIdle()
            val route = navController.currentBackStackEntry?.destination?.route
            Assert.assertTrue(route?.contains(Destination.BookDetailsScreen.route) ?: false)
            onNode(hasText(targetBook.title)).assertIsDisplayed()
        }

        Thread.sleep(2000)
    }

    // 2. Test pro zobrazení správných detailů knihy
    @Test
    fun test_book_detail_screen_displays_correct_book_details() {
        launchBookshelfScreenWithNavigation()
        val targetBook = DatabaseMock.book1

        with(composeRule) {
            onNodeWithTag(TestTagMainMenuBookshelfListOfBooksLazyList).assertIsDisplayed()

            onNode(hasText(targetBook.title)).assertIsDisplayed()
            onNode(hasText(targetBook.title)).performClick()
            mainClock.advanceTimeBy(1000)
            waitForIdle()

            val route = navController.currentBackStackEntry?.destination?.route
            Assert.assertTrue(route?.contains(Destination.BookDetailsScreen.route) ?: false)

            onNode(hasText(targetBook.title)).assertIsDisplayed()
            onNode(hasText(targetBook.author)).assertIsDisplayed()
            onNode(hasText(targetBook.pageCount.toString())).assertIsDisplayed()
            targetBook.genre?.let { onNode(hasText(it)).assertIsDisplayed() }
            targetBook.isbn?.let { onNode(hasText(it)).assertIsDisplayed() }
        }
    }

    // 3. Test smazání knihy z knihovny
    @Test
    fun test_delete_book_from_bookshelf() {
        launchBookshelfScreenWithNavigation()
        val targetBook = DatabaseMock.book1

        with(composeRule) {
            onNodeWithTag(TestTagMainMenuBookshelfListOfBooksLazyList).assertIsDisplayed()
            val bookCountPreDelete = DatabaseMock.booksList.count()
            assertTrue(bookCountPreDelete == 2)

            onNode(hasText(targetBook.title)).assertIsDisplayed()
            onNode(hasText(targetBook.title)).performClick()
            mainClock.advanceTimeBy(1000)
            waitForIdle()

            val route = navController.currentBackStackEntry?.destination?.route
            Assert.assertTrue(route?.contains(Destination.BookDetailsScreen.route) ?: false)

            onNodeWithTag(TestTagDeleteButton, useUnmergedTree = true).assertExists()
            onNodeWithTag(TestTagDeleteButton, useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag(TestTagDeleteButton, useUnmergedTree = true).performClick()
            mainClock.advanceTimeBy(1000)
            waitForIdle()

            val routeM = navController.currentBackStackEntry?.destination?.route
            Assert.assertTrue(routeM?.contains(Destination.MainMenuScreen.route) ?: false)
            val bookCountPostDelete = DatabaseMock.booksList.count()
            assertTrue(bookCountPostDelete == 1)
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