package cz.mendelu.bookwatchman.ui.screens.book_detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import cz.mendelu.bookwatchman.R
import cz.mendelu.bookwatchman.database.Book
import cz.mendelu.bookwatchman.database.BookState
import cz.mendelu.bookwatchman.navigation.INavigationRouter
import cz.mendelu.bookwatchman.ui.elements.BaseScreen
import cz.mendelu.bookwatchman.ui.elements.BookDetailSectionCard
import cz.mendelu.bookwatchman.ui.theme.basicCornerShape
import cz.mendelu.bookwatchman.ui.theme.basicPadding
import java.io.File

const val TestTagBookDetailImage = "book_image"
const val TestTagBookTitle = "book_title"
const val TestTagBookAuthor = "book_author"
const val TestTagBookPageCount = "book_page_count"
const val TestTagBookStatusDropdown = "book_status_dropdown"
const val TestTagPagesReadInput = "pages_read_input"
const val TestTagReadingProgressBar = "reading_progress_bar"
const val TestTagSaveButton = "save_button"
const val TestTagDeleteButton = "delete_button"

@Composable
fun BookDetailsScreen(
    navigation: INavigationRouter,
    bookId: Long?
){
    val viewModel = hiltViewModel<BookDetailViewModel>()

    val book = remember { mutableStateOf<Book?>(null) }

    val state = viewModel.uiState.collectAsStateWithLifecycle()
    state.value.let {
        when(it){
            BookDetailUIState.Loading -> {
                viewModel.loadBook(bookId)
            }
            is BookDetailUIState.DataLoaded -> {
                book.value = it.book
            }
            is BookDetailUIState.Changed -> {
                book.value = it.book
            }
        }
    }

    val isChanged by viewModel.isChanged.collectAsStateWithLifecycle()

    BaseScreen(
        showLoading = state.value is BookDetailUIState.Loading,
        topBarText = stringResource(R.string.book_details),
        onBackClick = {
            navigation.returnBack()
        },
        actions = {
            if (isChanged) {
                IconButton(onClick = {
                    book.value?.let { viewModel.saveBook(it) }
                }) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = stringResource(R.string.save),
                        modifier = Modifier.testTag(TestTagSaveButton)
                    )
                }
            }
            IconButton(onClick = {
                book.value?.let {
                    viewModel.deleteBook(it)
                    navigation.returnBack()
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete),
                    modifier = Modifier.testTag(TestTagDeleteButton)
                )
            }
        }
    ) {
        BookDetailsScreenContent(
            paddingValues = it,
            viewModel = viewModel,
            book = book.value
        )
    }
}

@Composable
fun BookDetailsScreenContent(
    paddingValues: PaddingValues,
    viewModel: BookDetailViewModel,
    book: Book?
) {
    if (book != null) {
        val scrollState = rememberLazyListState()
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(basicCornerShape()),
            state = scrollState,
        ) {
            item {
                val context = LocalContext.current
                val selectedImage = book.pictureUri?.let {
                    File(context.filesDir, it).absolutePath
                }
                val displayImageUrl = selectedImage?.takeIf { it.isNotEmpty() }
                    ?: R.drawable.ic_launcher_foreground
                if (!selectedImage.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .height(550.dp)
                            .fillMaxWidth()
                            .offset { IntOffset(0, (scrollState.firstVisibleItemScrollOffset / 2)) }
                            .padding(basicPadding())
                    ) {
                        AsyncImage(
                            model = displayImageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(16.dp))
                                .clipToBounds()
                                .testTag(TestTagBookDetailImage)
                        )
                    }
                }
            }
            item {
                BookDetailSectionCard(
                    title = ""
                ) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.testTag(TestTagBookTitle)
                    )
                    Text(
                        text = "${stringResource(R.string.author)}: ${book.author}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.testTag(TestTagBookAuthor)
                    )
                }
            }
            item {
                BookDetailSectionCard(
                    title = stringResource(R.string.book_details)
                ) {
                    Text(
                        text = "${stringResource(R.string.total_pages)}: ${book.pageCount}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.testTag(TestTagBookPageCount)
                    )
                    val selectedState = book.bookState
                    BookStatusDropdown(
                        book = book,
                        viewModel = viewModel
                    )
                    AnimatedVisibility(
                        visible = selectedState == BookState.READING,
                        enter = fadeIn(tween(300)) + slideInVertically(tween(300)),
                        exit = fadeOut(tween(300)) + slideOutVertically(tween(300))
                    ) {
                        Column {
                            PagesReadInput(
                                book = book,
                                viewModel = viewModel
                            )
                            ReadingProgressBar(book = book)
                        }
                    }
                    book.genre?.let {
                        Text(
                            text = "${stringResource(R.string.genre)}: $it",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    book.isbn?.let {
                        Text(
                            text = "ISBN: $it",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            item {
                book.description?.let { description ->
                    BookDetailSectionCard(
                        title = stringResource(R.string.description)
                    ) {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BookStatusDropdown(
    book: Book,
    viewModel: BookDetailViewModel
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var selectedState by remember { mutableStateOf(book.bookState) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.small
            )
            .padding(12.dp)
            .testTag(TestTagBookStatusDropdown)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = selectedState.getDisplayName(context),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            BookState.entries.forEach { state ->
                DropdownMenuItem(
                    text = { Text(text = state.getDisplayName(context)) },
                    onClick = {
                        expanded = false
                        selectedState = state
                        viewModel.onStateChange(state, book)
                    }
                )
            }
        }
    }
}

@Composable
fun PagesReadInput(
    book: Book,
    viewModel: BookDetailViewModel
) {
    val pattern = remember { Regex("^\\d*$") }
    var pagesReadCount by remember { mutableStateOf(book.pagesRead?.toString() ?: "") }
    val maxPages = book.pageCount

    OutlinedTextField(
        value = pagesReadCount,
        onValueChange = { newText ->
            if (newText.isEmpty() || newText.matches(pattern)) {
                val newCount = newText.toIntOrNull() ?: 0

                if (newCount <= maxPages) {
                    pagesReadCount = newText
                    viewModel.onPagesReadChange(newCount, book)
                }
            }
        },
        label = { Text(stringResource(R.string.pages_read)) },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(TestTagPagesReadInput)
    )
}

@Composable
fun ReadingProgressBar(book: Book) {
    val pagesRead = book.pagesRead ?: 0
    val totalPages = book.pageCount

    val progress = if (totalPages > 0) pagesRead.toFloat() / totalPages else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(750),
        label = ""
    )

    LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = Modifier
            .fillMaxWidth()
            .padding(basicPadding())
            .testTag(TestTagReadingProgressBar),
        color = MaterialTheme.colorScheme.primary,
    )
}
