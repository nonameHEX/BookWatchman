package cz.mendelu.bookwatchman.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.mendelu.bookwatchman.R
import cz.mendelu.bookwatchman.model.SearchBarOption
import cz.mendelu.bookwatchman.ui.theme.basicCornerShape
import cz.mendelu.bookwatchman.ui.theme.basicPadding

@Composable
fun MySearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isDropdownShown: Boolean = false,
    searchOption: SearchBarOption = SearchBarOption.TITLE,
    onSearchOptionChange: (SearchBarOption) -> Unit = {},
    onClearText: () -> Unit = {},
    placeholder: String = stringResource(R.string.search)
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = basicPadding(),
                vertical = basicPadding() / 2
            )
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(basicCornerShape())
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                shape = RoundedCornerShape(basicCornerShape())
            )
            .padding(
                horizontal = basicPadding(),
                vertical = basicPadding() / 4)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.weight(0.05f))
            BasicTextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    if (searchText.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        )
                    }
                    innerTextField()
                }
            )

            if(isDropdownShown){
                Box {
                    Text(
                        text = searchOption.getDisplayName(LocalContext.current),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.clickable { expanded = true }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        SearchBarOption.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.getDisplayName(LocalContext.current)) },
                                onClick = {
                                    onSearchOptionChange(option)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            IconButton(
                onClick = { onClearText() },

                ) {
                Icon(
                    imageVector = Icons.Outlined.Cancel,
                    contentDescription = null)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCustomSearchBar() {
    MySearchBar(
        searchText = "",
        onSearchTextChange = {},
        placeholder = "Hledat"
    )
}