package cz.mendelu.bookwatchman.ui.elements

import android.util.Log
import android.webkit.URLUtil
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import cz.mendelu.bookwatchman.R
import cz.mendelu.bookwatchman.ui.theme.basicCornerShape
import cz.mendelu.bookwatchman.ui.theme.basicPadding

@Composable
fun SearchHorizontalCard(
    modifier: Modifier = Modifier,
    title: String,
    authors: List<String>,
    pageCount: Int,
    imageUrl: String?,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .padding(basicPadding())
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(basicCornerShape()),
        onClick = onClick,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding((basicPadding() / 2)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val isValidUrl = URLUtil.isValidUrl(imageUrl)
            AsyncImage(
                model = if (isValidUrl) imageUrl else R.drawable.ic_launcher_foreground,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp),
                onError = {
                    Log.e("BookImage", "Error loading image: ${it.result.throwable}")
                }
            )

            Column(
                modifier = Modifier
                    .padding(start = (basicPadding() / 2))
                    .weight(1f)
            ) {
                if(title.length > 35){
                    Text(
                        text = title.take(35) + "...",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = authors.joinToString(", "),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "${stringResource(R.string.pages)}: $pageCount",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.weight(0.1f))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null
            )
        }
    }
}