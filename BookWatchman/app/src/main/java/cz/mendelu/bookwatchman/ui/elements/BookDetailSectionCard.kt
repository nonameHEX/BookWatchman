package cz.mendelu.bookwatchman.ui.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.mendelu.bookwatchman.ui.theme.basicCardElevation
import cz.mendelu.bookwatchman.ui.theme.basicContentSpacing
import cz.mendelu.bookwatchman.ui.theme.basicCornerShape
import cz.mendelu.bookwatchman.ui.theme.basicPadding

@Composable
fun BookDetailSectionCard(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable () -> Unit
){
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = basicPadding()),
        shape = RoundedCornerShape(basicCornerShape()),
        elevation = CardDefaults.cardElevation(basicCardElevation())
    ) {
        Column(
            modifier = Modifier
                .padding(basicPadding()),
            verticalArrangement = Arrangement.spacedBy(basicContentSpacing())
        ) {
            if (title.isNotEmpty()){
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            content()
        }
    }
}