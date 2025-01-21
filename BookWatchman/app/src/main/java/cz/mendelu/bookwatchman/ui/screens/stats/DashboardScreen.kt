package cz.mendelu.bookwatchman.ui.screens.stats

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import cz.mendelu.bookwatchman.R
import cz.mendelu.bookwatchman.database.Book
import cz.mendelu.bookwatchman.database.BookState
import cz.mendelu.bookwatchman.navigation.INavigationRouter
import cz.mendelu.bookwatchman.ui.elements.BaseScreen
import cz.mendelu.bookwatchman.ui.elements.ThemedButton
import cz.mendelu.bookwatchman.ui.theme.basicContentSpacing
import cz.mendelu.bookwatchman.ui.theme.basicPadding

const val TestTagStatsScreenButton = "to_my_bookshelf_button"

@Composable
fun StatsScreen(
    navigation: INavigationRouter
) {
    val viewModel = hiltViewModel<DashboardViewModel>()

    val books = remember { mutableStateListOf<Book>() }

    val state = viewModel.uiState.collectAsStateWithLifecycle()

    state.value.let {
        when(it){
            DashboardUIState.Loading -> {
                viewModel.loadUsername()
            }
            is DashboardUIState.DataLoaded -> {
                books.clear()
                books.addAll(it.books)
            }
        }
    }

    val username = viewModel.userName.collectAsStateWithLifecycle()
    BaseScreen(
        showLoading = state.value is DashboardUIState.Loading,
    ){
        StatsScreenContent(
            paddingValues = it,
            navigation = navigation,
            books = books,
            username = username.value
        )
    }
}

@Composable
fun StatsScreenContent(
    paddingValues: PaddingValues,
    navigation: INavigationRouter,
    books: List<Book>,
    username: String
) {
    val readBooks = books.filter { it.state == BookState.READ.value }
    val readingBooks = books.filter { it.state == BookState.READING.value }
    val toReadBooks = books.filter { it.state == BookState.TO_READ.value }
    val totalPagesRead = books.sumOf {
        when (it.state) {
            BookState.READ.value -> it.pageCount
            BookState.READING.value -> it.pagesRead ?: 0
            else -> 0
        }
    }

    LazyColumn(modifier = Modifier
        .padding(paddingValues)
        .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy((basicContentSpacing()))
    ) {
        item {
            Image(
                painter = rememberAsyncImagePainter(model = R.mipmap.ic_launcher_new),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
            Text(text = "${stringResource(R.string.welcome)} ${username.takeIf { it.isNotBlank() } ?: ""}")

        }
        item {
            StatsChip(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.HourglassEmpty,
                        contentDescription = null
                    )
                },
                text = "${stringResource(R.string.number_of_books_to_read)}: ${toReadBooks.size}"
            )
        }
        item {
            StatsChip(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Bookmark,
                        contentDescription = null
                    )
                },
                text = "${stringResource(R.string.number_of_books_reading)}: ${readingBooks.size}"
            )
        }
        item {
            StatsChip(
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Bookmark,
                        contentDescription = null
                    )
                },
                text = "${stringResource(R.string.number_of_books_read)}: ${readBooks.size}"
            )
        }
        item {
            StatsChip(
                icon = {
                    Icon(
                        imageVector = Icons.Default.TextFields,
                        contentDescription = null
                    )
                },
                text = "${stringResource(R.string.total_pages_read)}: $totalPagesRead"
            )
        }
        item {
            Image(
                modifier = Modifier
                    .padding(basicPadding()),
                painter = painterResource(id = R.drawable.stats_screen_image),
                contentDescription = "Image"
            )
        }
        item {
            ThemedButton(
                modifier = Modifier.testTag(TestTagStatsScreenButton),
                onClick = {
                navigation.navigateToMainMenuScreen()
            }
            ){
                Text(text = stringResource(R.string.to_my_bookshelf))
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowForward,
                    contentDescription = stringResource(R.string.to_my_bookshelf)
                )
            }
        }
    }
}

@Composable
fun StatsChip(
    icon: @Composable () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    AssistChip(
        onClick = {},
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
            .padding(horizontal = basicPadding()),
        label = {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon()
                Spacer(modifier = Modifier
                    .width(8.dp))
                Text(text = text)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewStatsScreen() {
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
    StatsScreenContent(
        paddingValues = PaddingValues(16.dp),
        navigation = mockNavigation,
        books = listOf(),
        username = "John Doe"
    )
}