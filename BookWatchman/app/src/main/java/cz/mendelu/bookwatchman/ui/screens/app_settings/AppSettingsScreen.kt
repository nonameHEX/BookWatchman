package cz.mendelu.bookwatchman.ui.screens.app_settings

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.mendelu.bookwatchman.R
import cz.mendelu.bookwatchman.model.BookSortOrderOption
import cz.mendelu.bookwatchman.navigation.INavigationRouter
import cz.mendelu.bookwatchman.ui.elements.BaseScreen
import cz.mendelu.bookwatchman.ui.theme.basicContentSpacing
import cz.mendelu.bookwatchman.ui.theme.basicPadding

@Composable
fun AppSettingsScreen(
    navigation: INavigationRouter
){
    val viewModel = hiltViewModel<AppSettingsViewModel>()

    val state = viewModel.uiState.collectAsStateWithLifecycle()
    state.value.let {
        when(it){
            AppSettingsUIState.Loading -> {}
            AppSettingsUIState.DataLoaded -> {}
            AppSettingsUIState.SettingsChanged -> {}
        }
    }

    BaseScreen(
        showLoading = state.value is AppSettingsUIState.Loading,
        topBarText = stringResource(R.string.options),
        onBackClick = {
            navigation.returnBack()
        }
    ){
        AppSettingsScreenContent(
            paddingValues = it,
            viewModel = viewModel,
        )
    }
}

@Composable
fun AppSettingsScreenContent(
    paddingValues: PaddingValues,
    viewModel: AppSettingsViewModel
){
    LaunchedEffect(Unit) {
        viewModel.startDebouncedSaveUsername()
    }

    LazyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .padding(basicPadding()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(basicContentSpacing())
    ) {
        item {
            val userName = viewModel.userName.collectAsStateWithLifecycle()
            val userTextValue = userName.value
            Text(text = stringResource(R.string.username_for_personalized_welcome_in_dashboard))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = userTextValue,
                onValueChange = { viewModel.onUserNameChanged(it) },
                label = { Text(text = stringResource(R.string.username)) },
                singleLine = true
            )
        }
        item {
            Text(text = stringResource(R.string.sort_books_in_bookshelf_by))
            BookSortOrderDropdown(viewModel = viewModel)
        }
        item {
            val context = LocalContext.current
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }) {
                Text(text = stringResource(R.string.allow_permission_in_app_settings))
            }
        }
        item {
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    viewModel.deleteAllBooks()
                }
            ) {
                Text(text = stringResource(R.string.delete_all_books_from_bookshelf))
            }
        }
    }
}

@Composable
fun BookSortOrderDropdown(
    viewModel: AppSettingsViewModel
){
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val selectedState by viewModel.sortOrder.collectAsStateWithLifecycle()

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
            BookSortOrderOption.entries.forEach { state ->
                DropdownMenuItem(
                    text = { Text(text = state.getDisplayName(context)) },
                    onClick = {
                        expanded = false
                        viewModel.onSortOrderChanged(state)
                    }
                )
            }
        }
    }
}