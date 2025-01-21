package cz.mendelu.bookwatchman.ui.screens.main_menu_bookshelf

import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import cz.mendelu.bookwatchman.R
import cz.mendelu.bookwatchman.database.Book
import cz.mendelu.bookwatchman.database.BookState
import cz.mendelu.bookwatchman.navigation.Destination
import cz.mendelu.bookwatchman.navigation.INavigationRouter
import cz.mendelu.bookwatchman.ui.elements.BaseScreen
import cz.mendelu.bookwatchman.ui.elements.MySearchBar
import cz.mendelu.bookwatchman.ui.elements.PlaceHolderScreen
import cz.mendelu.bookwatchman.ui.elements.PlaceholderScreenContent
import cz.mendelu.bookwatchman.ui.theme.basicCornerShape
import cz.mendelu.bookwatchman.ui.theme.basicPadding
import java.io.File

const val TestTagMainMenuScreenLocationButton = "location_button"
const val TestTagMainMenuScreenSettingsButton = "settings_button"
const val TestTagMainMenuScreenAddButton = "add_button"
const val TestTagMainMenuBookshelfSearchBar = "search_bar"
const val TestTagMainMenuBookshelfListOfBooksLazyList = "list_of_books_lazy_list"
const val TestTagMainMenuBookshelfBookCard = "book_card"
const val TestTagMainMenuBookshelfBookTitle = "book_title"
const val TestTagMainMenuBookshelfBookState = "book_state"

@Composable
fun MainMenuBookshelfScreen(
    navigation: INavigationRouter
) {
    val viewModel = hiltViewModel<MainMenuBookshelfViewModel>()

    val books = remember { mutableStateListOf<Book>() }

    val state = viewModel.uiState.collectAsStateWithLifecycle()

    state.value.let {
        when(it){
            MainMenuBookshelfUIState.Loading -> {
                viewModel.loadBooks()
            }
            is MainMenuBookshelfUIState.DataLoaded -> {
                books.clear()
                books.addAll(it.books)
            }
        }
    }

    val navController = navigation.getNavController()
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            if (backStackEntry.destination.route == Destination.MainMenuScreen.route) {
                viewModel.loadBooks()
            }
        }
    }

    BaseScreen(
        showLoading = state.value is MainMenuBookshelfUIState.Loading,
        actions = {
            Row {
                IconButton(
                    modifier = Modifier.testTag(TestTagMainMenuScreenLocationButton),
                    onClick = { navigation.navigateToMapScreen() }) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = stringResource(R.string.stats)
                    )
                }
                IconButton(
                    modifier = Modifier.testTag(TestTagMainMenuScreenSettingsButton),
                    onClick = { navigation.navigateToAppSettingsScreen() }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.options)
                    )
                }
            }
        },
        topBarText = stringResource(R.string.bookshelf),
        onBackClick = {
            navigation.returnBack()
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.testTag(TestTagMainMenuScreenAddButton),
                onClick = {
                    navigation.navigateToChooseAddOptionScreen()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_book)
                )
            }
        }
    ){
        MainMenuBookshelfScreenContent(
            paddingValues = it,
            navigation = navigation,
            books = books
        )
    }
}

@Composable
fun MainMenuBookshelfScreenContent(
    paddingValues: PaddingValues,
    navigation: INavigationRouter,
    books: List<Book>,
) {
    if(books.isEmpty()){
        PlaceHolderScreen(
            modifier = Modifier
                .padding(paddingValues),
            content = PlaceholderScreenContent(
                image = R.drawable.bookshelf,
                title = stringResource(R.string.you_don_t_have_any_books_in_your_bookshelf),
                text = stringResource(R.string.add_your_first_book),
            )
        )
    }else{
        var searchText by remember { mutableStateOf("") }
        val filteredItems = remember(searchText) {
            if (searchText.isEmpty()) books
            else books.filter {
                it.title.contains(searchText, ignoreCase = true) ||
                        it.author.contains(searchText, ignoreCase = true)
            }
        }

        Log.d("MainMenuBookshelfScreenContent", "books: $books")

        Column(
            modifier = Modifier
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MySearchBar(
                modifier = Modifier.testTag(TestTagMainMenuBookshelfSearchBar),
                searchText = searchText,
                onSearchTextChange = { searchText = it },
                placeholder = stringResource(R.string.search),
            )

            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = basicPadding())
                    .testTag(TestTagMainMenuBookshelfListOfBooksLazyList),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(items = filteredItems, key = { it.id ?: 0L }) { book ->
                    HorizontalCard(
                        modifier = Modifier
                            .testTag(TestTagMainMenuBookshelfBookCard)
                            .animateItem(
                            fadeInSpec = null,
                            fadeOutSpec = null,
                            placementSpec = tween(durationMillis = 500)
                        ),
                        book = book,
                        onClick = {
                            navigation.navigateToBookDetailScreen(book.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HorizontalCard(
    modifier: Modifier = Modifier,
    book: Book,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .padding(basicPadding())
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(basicCornerShape()),
        onClick = onClick,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ){
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding((basicPadding() / 4)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val selectedImage = book.pictureUri?.let {
                File(context.filesDir, it).absolutePath
            }
            val displayImageUrl = selectedImage?.takeIf { it.isNotEmpty() }
                ?: R.drawable.ic_launcher_foreground

            AsyncImage(
                model = displayImageUrl,
                contentDescription = "Book cover",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape((basicCornerShape() * 2))),
            )
            Column(modifier = Modifier
                .weight(1f)
            ) {
                Text(
                    modifier = Modifier.testTag(TestTagMainMenuBookshelfBookTitle),
                    text = book.title,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (book.bookState == BookState.READING) {
                    Text(
                        modifier = Modifier.testTag(TestTagMainMenuBookshelfBookState),
                        text = "${book.bookState.getDisplayName(context)} ${stringResource(R.string.on_page)} ${book.pagesRead}")
                }else{
                    Text(
                        modifier = Modifier.testTag(TestTagMainMenuBookshelfBookState),
                        text = book.bookState.getDisplayName(context))
                }
            }
            Image(imageVector = Icons.AutoMirrored.Default.ArrowForward, contentDescription = null)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainMenuBookshelfScreen(){
    val mockNavigation = object : INavigationRouter {
        override fun getNavController(): NavController {
            TODO("Not yet implemented")
        }
        override fun returnBack() {}
        override fun navigateToStatsScreen() {}
        override fun navigateToMainMenuScreen() {}
        override fun navigateToChooseAddOptionScreen() {}
        override fun navigateToSearchOnlineScreen() {}
        override fun navigateToSearchScanScreen() {}
        override fun navigateToAddBookScreen(id: String?) {}
        override fun navigateToBookDetailScreen(id: Long?) {}
        override fun navigateToMapScreen() {}
        override fun navigateToAppSettingsScreen() {}
    }
    MainMenuBookshelfScreenContent(
        paddingValues = PaddingValues(16.dp),
        navigation = mockNavigation,
        books = listOf()
    )
}
