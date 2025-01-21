package cz.mendelu.bookwatchman.ui.elements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cz.mendelu.bookwatchman.ui.theme.basicMargin

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
){
    Box(modifier = modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = basicMargin(), bottom = basicMargin())
        )
    }
}
