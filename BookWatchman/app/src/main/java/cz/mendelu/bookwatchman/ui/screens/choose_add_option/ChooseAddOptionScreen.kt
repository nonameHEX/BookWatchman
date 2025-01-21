package cz.mendelu.bookwatchman.ui.screens.choose_add_option

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cz.mendelu.bookwatchman.R
import cz.mendelu.bookwatchman.navigation.INavigationRouter
import cz.mendelu.bookwatchman.ui.elements.BaseScreen
import cz.mendelu.bookwatchman.ui.elements.ThemedButton
import cz.mendelu.bookwatchman.ui.theme.basicMargin

const val TestTagChooseAddManualButton = "add_manual_button"
const val TestTagChooseSearchOnlineButton = "search_online_button"

@Composable
fun ChooseAddOptionScreen(
    navigation: INavigationRouter
){
    BaseScreen(
        topBarText = stringResource(R.string.choose_add_option),
        onBackClick = {
            navigation.returnBack()
        }
    ){
        ChooseAddOptionScreenContent(
            paddingValues = it,
            navigation = navigation
        )
    }
}

@Composable
fun ChooseAddOptionScreenContent(
    paddingValues: PaddingValues,
    navigation: INavigationRouter,
) {
    Column(
        modifier = Modifier.padding(paddingValues).padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        ThemedButton(onClick = {
            navigation.navigateToSearchOnlineScreen()
        }
        ){
            Text(
                modifier = Modifier.testTag(TestTagChooseSearchOnlineButton),
                text = stringResource(R.string.online_text_search))
            Icon(
                modifier = Modifier.padding(start = 8.dp),
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.online_text_search)
            )
        }

        ThemedButton(onClick = {
            navigation.navigateToSearchScanScreen()
        }
        ){
            Text(text = stringResource(R.string.scan_book))
            Icon(
                modifier = Modifier.padding(start = 8.dp),
                imageVector = Icons.Outlined.CameraAlt,
                contentDescription = stringResource(R.string.scan_book)
            )
        }

        ThemedButton(
            modifier = Modifier.testTag(TestTagChooseAddManualButton),
            onClick = {
            navigation.navigateToAddBookScreen(null)
        }
        ){
            Text(text = stringResource(R.string.add_book_manually))
            Icon(
                modifier = Modifier.padding(start = 8.dp),
                imageVector = Icons.Outlined.Create,
                contentDescription = stringResource(R.string.add_book_manually)
            )
        }

        Image(
            modifier = Modifier.padding(basicMargin()),
            painter = painterResource(id = R.drawable.searching_screen_image),
            contentDescription = null
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewChooseAddOptionScreen() {
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
    ChooseAddOptionScreenContent(
        paddingValues = PaddingValues(16.dp),
        navigation = mockNavigation,
    )
}