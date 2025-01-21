package cz.mendelu.bookwatchman.ui.screens.search_online

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.mendelu.bookwatchman.R
import cz.mendelu.bookwatchman.communication.model.BookResponse
import cz.mendelu.bookwatchman.navigation.INavigationRouter
import cz.mendelu.bookwatchman.ui.elements.BaseScreen
import cz.mendelu.bookwatchman.ui.elements.MySearchBar
import cz.mendelu.bookwatchman.ui.elements.SearchHorizontalCard
import cz.mendelu.bookwatchman.ui.theme.basicPadding
import kotlinx.coroutines.delay

@Composable
fun SearchOnlineScreen(
    navigation: INavigationRouter
){
    val viewModel = hiltViewModel<SearchOnlineViewModel>()

    val foundBooks = remember { mutableStateOf<BookResponse?>(null) }

    var showErrorSnackbar by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val state = viewModel.uiState.collectAsStateWithLifecycle()
    state.value.let {
        when(it){
            SearchOnlineUIState.Loading -> {}
            SearchOnlineUIState.ReadyForSearch -> {
                foundBooks.value = null
            }
            is SearchOnlineUIState.DataLoaded -> {
                foundBooks.value = it.books
            }
            is SearchOnlineUIState.Error -> {
                if (it.error.communicationError == R.string.empty_response_body) {
                    showErrorSnackbar = false
                } else {
                    showErrorSnackbar = true
                    errorMessage = stringResource(it.error.communicationError)
                }
            }
        }
    }

    BaseScreen(
        showLoading = state.value is SearchOnlineUIState.Loading,
        topBarText = stringResource(R.string.online_text_search),
        onBackClick = {
            navigation.returnBack()
        }
    ){
        SearchOnlineScreenContent(
            paddingValues = it,
            navigation = navigation,
            foundBooks = foundBooks.value,
            viewModel = viewModel
        )

        if (showErrorSnackbar) {
            Snackbar(
                action = {
                    TextButton(onClick = { showErrorSnackbar = false }) {
                        Text("OK")
                    }
                },
                containerColor = Color.Red,
                modifier = Modifier.padding(it)
            ) { Text(errorMessage) }

            LaunchedEffect(showErrorSnackbar) {
                if (showErrorSnackbar) {
                    delay(3000)
                    showErrorSnackbar = false
                }
            }
        }
    }
}

@Composable
fun SearchOnlineScreenContent(
    paddingValues: PaddingValues,
    navigation: INavigationRouter,
    foundBooks: BookResponse?,
    viewModel: SearchOnlineViewModel
){
    val searchText by viewModel.searchText.collectAsStateWithLifecycle()
    val searchOption by viewModel.searchOption.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.startDebouncedSearch()
    }

    Column(
        modifier = Modifier
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MySearchBar(
            searchText = searchText,
            onSearchTextChange = { viewModel.onSearchTextChanged(it) },
            isDropdownShown = true,
            searchOption = searchOption,
            onSearchOptionChange = { viewModel.onChangeSearchOption(it) },
            onClearText = { viewModel.onSearchTextChanged("") },
            placeholder = stringResource(R.string.search),
        )

        if(isSearching) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }

        LazyColumn(
            modifier = Modifier
                .padding(horizontal = basicPadding()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            foundBooks?.let {
                if (it.items.isNotEmpty()) {
                    items(items = it.items.take(30)) { bookItem ->
                        SearchHorizontalCard(
                            modifier = Modifier.animateItem(
                                fadeInSpec = null,
                                fadeOutSpec = null,
                                placementSpec = tween(durationMillis = 500)
                            ),
                            title = bookItem.volumeInfo.title,
                            authors = bookItem.volumeInfo.authors ?: listOf(stringResource(R.string.unknown_author)),
                            pageCount = bookItem.volumeInfo.pageCount ?: 0,
                            imageUrl = bookItem.volumeInfo.imageLinks?.thumbnail ?: bookItem.volumeInfo.imageLinks?.smallThumbnail,
                            onClick = {
                                navigation.navigateToAddBookScreen(bookItem.id)
                            }
                        )
                    }
                } else {
                    item {
                        Text(stringResource(R.string.no_books_found),
                            modifier = Modifier
                                .padding(top = basicPadding())
                        )
                    }
                }
            }
        }
    }
}
