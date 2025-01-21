package cz.mendelu.bookwatchman.ui.screens.search_scan

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import cz.mendelu.bookwatchman.R
import cz.mendelu.bookwatchman.communication.model.BookResponse
import cz.mendelu.bookwatchman.navigation.INavigationRouter
import cz.mendelu.bookwatchman.ui.elements.BaseScreen
import cz.mendelu.bookwatchman.ui.elements.SearchHorizontalCard
import cz.mendelu.bookwatchman.ui.theme.basicPadding
import kotlinx.coroutines.delay

@Composable
fun SearchScanScreen(
    navigation: INavigationRouter
) {
    val viewModel = hiltViewModel<SearchScanViewModel>()

    val foundBooks = remember { mutableStateOf<BookResponse?>(null) }

    var showErrorSnackbar by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val state = viewModel.uiState.collectAsStateWithLifecycle()
    state.value.let {
        when (it) {
            SearchScanUIState.Loading -> {}
            SearchScanUIState.ReadyForSearch -> {
                foundBooks.value = null
            }
            is SearchScanUIState.DataLoaded -> {
                foundBooks.value = it.books
            }
            is SearchScanUIState.Error -> {
                if (it.error.communicationError == R.string.empty_response_body) {
                    showErrorSnackbar = false
                } else {
                    showErrorSnackbar = true
                    errorMessage = stringResource(it.error.communicationError)
                }
            }
        }
    }

    var isPermissionGranted by remember { mutableStateOf(false) }

    RequestCameraPermission(
        onPermissionGranted = {
            isPermissionGranted = true
        },
        onPermissionDenied = {
            navigation.returnBack()
        }
    )

    BaseScreen(
        topBarText = stringResource(R.string.scan_book),
        onBackClick = {
            navigation.returnBack()
        }
    ) {
        if (isPermissionGranted) {
            SearchScanScreenContent(
                paddingValues = it,
                navigation = navigation,
                foundBooks = foundBooks.value,
                viewModel = viewModel
            )
        }

        if (showErrorSnackbar) {
            Snackbar(
                action = {
                    TextButton(onClick = { showErrorSnackbar = false }) {
                        Text(stringResource(R.string.ok))
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
fun SearchScanScreenContent(
    paddingValues: PaddingValues,
    navigation: INavigationRouter,
    foundBooks: BookResponse?,
    viewModel: SearchScanViewModel
) {
    val searchText by viewModel.searchText.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.startDebouncedSearch()
    }
    Column(
        modifier = Modifier.padding(paddingValues)
    ) {
        Box(
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth()
                .clipToBounds()
        ){
            CameraScreenContent(
                viewModel = viewModel
            )

            if(isSearching){
                Snackbar(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = Color.Green,
                    contentColor = Color.Black,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("${stringResource(R.string.searching_for_books_with)}: $searchText")
                    }
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Log.d("SearchScanScreenContent", "foundBooks: ${foundBooks?.items?.count()}")
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
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
}

@Composable
fun RequestCameraPermission(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    val context = LocalContext.current

    val permissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                onPermissionGranted()
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.camera_permission_denied_please_enable_it_in_the_app_settings),
                    Toast.LENGTH_SHORT
                ).show()
                onPermissionDenied()
            }
        }
    )

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            onPermissionGranted()
        } else {
            permissionRequest.launch(Manifest.permission.CAMERA)
        }
    }
}

@Composable
fun CameraScreenContent(
    modifier: Modifier = Modifier,
    viewModel: SearchScanViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember { LifecycleCameraController(context) }

    AndroidView(
        modifier = modifier,
        factory = {
            PreviewView(it).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }.also { previewView ->
                startTextRecognition(
                    context = context,
                    cameraController = cameraController,
                    lifecycleOwner = lifecycleOwner,
                    previewView = previewView,
                    onDetectedTextRecognition = { detectedText ->
                        viewModel.onSearchTextChanged(detectedText)
                    }
                )
            }
        }
    )
}

private fun startTextRecognition(
    context: Context,
    cameraController: LifecycleCameraController,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    onDetectedTextRecognition: (String) -> Unit
){
    val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    var lastAnalysisTime = System.currentTimeMillis()

    cameraController.setImageAnalysisAnalyzer(
        ContextCompat.getMainExecutor(context),
        MlKitAnalyzer(
            listOf(textRecognizer),
            COORDINATE_SYSTEM_VIEW_REFERENCED,
            ContextCompat.getMainExecutor(context)
        ) { result ->
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastAnalysisTime >= 5000) { // 2 seconds throttle
                lastAnalysisTime = currentTime

                val visionText = result.getValue(textRecognizer)
                if (visionText != null && visionText.text.isNotBlank()) {
                    val combinedText = visionText.text
                                .replace("\n", " ")
                                .replace(Regex("\\s+"), " ")
                                .trim()
                    onDetectedTextRecognition(combinedText.lowercase())
                }
            }
        }
    )

    cameraController.bindToLifecycle(lifecycleOwner)
    previewView.controller = cameraController
}