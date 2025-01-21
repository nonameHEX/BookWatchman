package cz.mendelu.bookwatchman.ui.screens.add_book

import android.util.Log
import android.webkit.URLUtil
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import cz.mendelu.bookwatchman.R
import cz.mendelu.bookwatchman.communication.model.BookItem
import cz.mendelu.bookwatchman.navigation.Destination
import cz.mendelu.bookwatchman.navigation.INavigationRouter
import cz.mendelu.bookwatchman.ui.elements.BaseScreen
import cz.mendelu.bookwatchman.ui.theme.basicContentSpacing
import cz.mendelu.bookwatchman.ui.theme.basicPadding
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

const val TestTagAddBookSaveButton = "add_book_save_button"
const val TestTagAddBookTitleInput = "add_book_title_input"
const val TestTagAddBookAuthorInput = "add_book_author_input"
const val TestTagAddBookPageCountInput = "add_book_page_count_input"
const val TestTagAddBookGenreInput = "add_book_genre_input"
const val TestTagAddBookCoverButton = "add_book_cover_button"

@Composable
fun AddBookScreen(
    navigation: INavigationRouter,
    bookId: String? = null,
){
    val viewModel = hiltViewModel<AddBookViewModel>()

    val book = remember { mutableStateOf<BookItem?>(null) }

    var data by remember { mutableStateOf(viewModel.data) }

    var showErrorSnackbar by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val state = viewModel.uiState.collectAsStateWithLifecycle()
    state.value.let {
        when(it) {
            AddBookUIState.Default ->{

            }
            AddBookUIState.Loading -> {
                viewModel.loadBook(bookId)
            }
            AddBookUIState.ManualAdd -> {
                viewModel.setData(null)
                viewModel.updateToDefault()
            }
            AddBookUIState.Changed -> {
                data = viewModel.data
                viewModel.updateToDefault()
            }
            is AddBookUIState.DataLoaded -> {
                Log.d("AddBookScreen", "Data loaded: ${it.book.id}, ${it.book.volumeInfo.title}")

                book.value = it.book
                viewModel.setData(it.book)
            }
            is AddBookUIState.Error -> {
                if (it.error.communicationError == R.string.empty_response_body) {
                    showErrorSnackbar = false
                } else {
                    showErrorSnackbar = true
                    errorMessage = stringResource(it.error.communicationError)
                }
            }

        }
    }

    val context = LocalContext.current
    BaseScreen(
        showLoading = state.value is AddBookUIState.Loading,
        topBarText = stringResource(R.string.add_book),
        onBackClick = {
            val oldFileName = data.book.pictureUri
            if (!oldFileName.isNullOrEmpty()) {
                val oldFile = File(context.filesDir, oldFileName)
                if (oldFile.exists()) {
                    oldFile.delete()
                }
            }
            navigation.returnBack()
        }
    ){
        AddBookScreenContent(
            paddingValues = it,
            navigation = navigation,
            book = book.value,
            viewModel = viewModel,
            data = data
        )

        if (showErrorSnackbar) {
            Snackbar(
                action = {
                    TextButton(onClick = { showErrorSnackbar = false }) {
                        Text("OK")
                    }
                },
                containerColor = Color.Red,
                modifier = Modifier
                    .padding(it)
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
fun AddBookScreenContent(
    paddingValues: PaddingValues,
    navigation: INavigationRouter,
    book: BookItem?,
    viewModel: AddBookViewModel,
    data: AddBookData
){
    LazyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .padding(basicPadding()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(basicContentSpacing())
    ) {
        item {
            val context = LocalContext.current
            val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia(),
                onResult = { uri ->
                    uri?.let {
                        val fileName = "image_${System.currentTimeMillis()}.jpg"
                        val outputFile = File(context.filesDir, fileName)

                        val oldFileName = data.book.pictureUri
                        if (!oldFileName.isNullOrEmpty()) {
                            val oldFile = File(context.filesDir, oldFileName)
                            if (oldFile.exists()) {
                                oldFile.delete()
                            }
                        }

                        val inputStream: InputStream? = context.contentResolver.openInputStream(it)
                        inputStream?.let { input ->
                            FileOutputStream(outputFile).use { output ->
                                input.copyTo(output)
                            }
                        }

                        viewModel.onPictureChange(fileName)
                    }
                }
            )

            val imageUrl = book?.volumeInfo?.imageLinks?.thumbnail ?: book?.volumeInfo?.imageLinks?.smallThumbnail
            val isValidUrl = imageUrl?.let { URLUtil.isValidUrl(it) } ?: false
            val hasDownloadedImage = remember { mutableStateOf(false) }

            LaunchedEffect(imageUrl) {
                if (isValidUrl && !hasDownloadedImage.value) {
                    viewModel.downloadBookImage(imageUrl!!, context.filesDir)
                    hasDownloadedImage.value = true
                }
            }

            val selectedImage = data.book.pictureUri?.let {
                File(context.filesDir, it).absolutePath
            }
            val displayImageUrl = selectedImage?.takeIf { it.isNotEmpty() }
                ?: if(isValidUrl) imageUrl!! else R.drawable.ic_launcher_foreground

            AsyncImage(
                model = displayImageUrl,
                contentDescription = stringResource(R.string.book_cover),
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth(),
            )
            TextButton(
                modifier = Modifier.testTag(TestTagAddBookCoverButton),
                onClick = {
                singlePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }) {
                Text(text = stringResource(R.string.choose_own_book_cover_from_gallery))
            }
        }
        item {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(TestTagAddBookTitleInput),
                value = data.book.title,
                onValueChange = { viewModel.onTitleChange(it) },
                label = { Text(text = "${stringResource(R.string.title)} ${stringResource(R.string.required)}") },
                singleLine = true
            )
        }
        item {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(TestTagAddBookAuthorInput),
                value = data.book.author,
                onValueChange = { viewModel.onAuthorChange(it)},
                label = { Text(text = "${stringResource(R.string.author)} ${stringResource(R.string.required)}") },
                singleLine = true
            )
        }
        item {
            val pattern = remember { Regex("^\\d*$") }

            var pageCount by remember { mutableStateOf(book?.volumeInfo?.printedPageCount?.toString() ?: "") }
            val maxPages = 999999
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(TestTagAddBookPageCountInput),
                value = pageCount,
                onValueChange = { newText ->
                    if (newText.isEmpty() || newText.matches(pattern)) {
                        val newCount = newText.toIntOrNull() ?: 0

                        if (newCount <= maxPages) {
                            pageCount = newText
                            viewModel.onPageCountChange(newCount)
                        }
                    }
                },
                label = { Text(text = "${stringResource(R.string.total_pages)} ${stringResource(R.string.required)}") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                singleLine = true
            )
        }
        item {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(TestTagAddBookGenreInput),
                value = data.book.genre!!,
                onValueChange = { viewModel.onGenreChange(it)},
                label = { Text(text = stringResource(R.string.genre)) },
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = data.book.isbn!!,
                onValueChange = { viewModel.onIsbnChange(it)},
                label = { Text(text = "ISBN") },
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = data.book.description!!,
                onValueChange = { viewModel.onDescriptionChange(it)},
                label = { Text(text = stringResource(R.string.description)) },
            )
        }

        item {
            Button(
                modifier = Modifier.testTag(TestTagAddBookSaveButton),
                enabled = data.book.title.isNotEmpty() && data.book.author.isNotEmpty() && data.book.pageCount != 0,
                onClick = {
                viewModel.saveBook()
                navigation
                    .getNavController()
                    .popBackStack(route = Destination.MainMenuScreen.route, inclusive = false)
            }){
                Text(text = stringResource(R.string.save))
            }
        }
    }
}