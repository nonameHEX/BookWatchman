package cz.mendelu.bookwatchman

import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import cz.mendelu.bookwatchman.mock.DatabaseMock
import cz.mendelu.bookwatchman.navigation.Destination
import cz.mendelu.bookwatchman.navigation.NavGraph
import cz.mendelu.bookwatchman.ui.activity.MainActivity
import cz.mendelu.bookwatchman.ui.screens.add_book.TestTagAddBookAuthorInput
import cz.mendelu.bookwatchman.ui.screens.add_book.TestTagAddBookGenreInput
import cz.mendelu.bookwatchman.ui.screens.add_book.TestTagAddBookPageCountInput
import cz.mendelu.bookwatchman.ui.screens.add_book.TestTagAddBookSaveButton
import cz.mendelu.bookwatchman.ui.screens.add_book.TestTagAddBookTitleInput
import cz.mendelu.bookwatchman.ui.screens.choose_add_option.TestTagChooseAddManualButton
import cz.mendelu.bookwatchman.ui.screens.main_menu_bookshelf.TestTagMainMenuScreenAddButton
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
class UIAddBookTests {
    private lateinit var navController: NavHostController

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    // 1. Test pro zobrazení add screeny
    @Test
    fun test_add_book_is_displayed() {
        launchBookshelfScreenWithNavigation()

        with(composeRule){
            waitForIdle()
            val routeMain = navController.currentBackStackEntry?.destination?.route
            Assert.assertTrue(routeMain?.contains(Destination.MainMenuScreen.route) ?: false)
            onNodeWithTag(TestTagMainMenuScreenAddButton).assertIsDisplayed()
            onNodeWithTag(TestTagMainMenuScreenAddButton).performClick()
            mainClock.advanceTimeBy(1000)
            waitForIdle()
            val routeChooseAdd = navController.currentBackStackEntry?.destination?.route
            Assert.assertTrue(routeChooseAdd?.contains(Destination.ChooseAddOptionScreen.route) ?: false)
            onNodeWithTag(TestTagChooseAddManualButton).assertIsDisplayed()
            onNodeWithTag(TestTagChooseAddManualButton).performClick()
            mainClock.advanceTimeBy(1000)
            waitForIdle()
            val routeAdd = navController.currentBackStackEntry?.destination?.route
            Assert.assertTrue(routeAdd?.contains(Destination.AddBookScreen.route) ?: false)
        }
    }

    // 2. Test pro zobrazení add screeny kde bude save tlačítko disabled (protože nebude vyplněný content v textfieldech)
    @Test
    fun test_add_book_save_button_init_disabled() {
        launchBookshelfScreenWithNavigation()

        with(composeRule){
            waitForIdle()
            val routeMain = navController.currentBackStackEntry?.destination?.route
            Assert.assertTrue(routeMain?.contains(Destination.MainMenuScreen.route) ?: false)
            onNodeWithTag(TestTagMainMenuScreenAddButton).assertIsDisplayed()
            onNodeWithTag(TestTagMainMenuScreenAddButton).performClick()
            mainClock.advanceTimeBy(1000)
            waitForIdle()
            val routeChooseAdd = navController.currentBackStackEntry?.destination?.route
            Assert.assertTrue(routeChooseAdd?.contains(Destination.ChooseAddOptionScreen.route) ?: false)
            onNodeWithTag(TestTagChooseAddManualButton).assertIsDisplayed()
            onNodeWithTag(TestTagChooseAddManualButton).performClick()
            mainClock.advanceTimeBy(1000)
            waitForIdle()
            val routeAdd = navController.currentBackStackEntry?.destination?.route
            Assert.assertTrue(routeAdd?.contains(Destination.AddBookScreen.route) ?: false)

            onNodeWithTag(TestTagAddBookSaveButton).assertIsDisplayed()
            onNodeWithTag(TestTagAddBookSaveButton).assertIsNotEnabled()
        }
    }

    // 3. Test pro zobrazení add screeny vyplnění údajů a uložení
    @Test
    fun test_add_book_fill_content_and_save() {
        launchBookshelfScreenWithNavigation()

        with(composeRule){
            waitForIdle()
            val routeMain = navController.currentBackStackEntry?.destination?.route
            Assert.assertTrue(routeMain?.contains(Destination.MainMenuScreen.route) ?: false)
            val bookCountPreAdd = DatabaseMock.booksList.count()
            assertTrue(bookCountPreAdd == 2)
            onNodeWithTag(TestTagMainMenuScreenAddButton).assertIsDisplayed()
            onNodeWithTag(TestTagMainMenuScreenAddButton).performClick()
            mainClock.advanceTimeBy(1000)
            waitForIdle()
            val routeChooseAdd = navController.currentBackStackEntry?.destination?.route
            Assert.assertTrue(routeChooseAdd?.contains(Destination.ChooseAddOptionScreen.route) ?: false)
            onNodeWithTag(TestTagChooseAddManualButton).assertIsDisplayed()
            onNodeWithTag(TestTagChooseAddManualButton).performClick()
            mainClock.advanceTimeBy(1000)
            waitForIdle()
            val routeAdd = navController.currentBackStackEntry?.destination?.route
            Assert.assertTrue(routeAdd?.contains(Destination.AddBookScreen.route) ?: false)

            onNodeWithTag(TestTagAddBookTitleInput).performTextInput("Testovací Kniha")
            onNodeWithTag(TestTagAddBookAuthorInput).performTextInput("Testovací Autor")
            onNodeWithTag(TestTagAddBookPageCountInput).performTextInput("250")
            onNodeWithTag(TestTagAddBookGenreInput).performTextInput("Fikce")

            onNodeWithTag(TestTagAddBookSaveButton).assertIsDisplayed()
            onNodeWithTag(TestTagAddBookSaveButton).assertIsEnabled()
            onNodeWithTag(TestTagAddBookSaveButton).performClick()
            mainClock.advanceTimeBy(1000)
            waitForIdle()
            val routeMainMenu = navController.currentBackStackEntry?.destination?.route
            Assert.assertTrue(routeMainMenu?.contains(Destination.MainMenuScreen.route) ?: false)

            val bookCountPostAdd = DatabaseMock.booksList.count()
            assertTrue(bookCountPostAdd == 3)
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