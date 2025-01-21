package cz.mendelu.bookwatchman.ui.elements

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.mendelu.bookwatchman.R
import cz.mendelu.bookwatchman.ui.screens.map.PlacesResponse
import cz.mendelu.bookwatchman.ui.screens.map.getLocalizedPlaceType
import cz.mendelu.bookwatchman.ui.theme.SlightPurpleWhiteDarker
import cz.mendelu.bookwatchman.ui.theme.basicCornerShape
import cz.mendelu.bookwatchman.ui.theme.basicPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapBottomSheet(
    place: PlacesResponse.Place,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(basicPadding())
        ) {
            Text(
                text = place.name ?: stringResource(R.string.unknown_place),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = basicPadding())
            )
            place.rating?.let { rating ->
                RatingRow(rating)
            }

            place.address?.let { address ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = basicPadding() / 2),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1.1f)
                    ) {
                        InfoRow(
                            label = stringResource(R.string.address),
                            value = address,
                            icon = Icons.Default.NearMe
                        )
                    }

                    IconButton(
                        modifier = Modifier
                            .clip(RoundedCornerShape(basicCornerShape()))
                            .background(SlightPurpleWhiteDarker),
                        onClick = {
                        val clip = ClipData.newPlainText("address", address)
                        clipboard.setPrimaryClip(clip)
                    }) {
                        Icon(
                            Icons.Filled.ContentCopy,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        modifier = Modifier
                            .clip(RoundedCornerShape(basicCornerShape()))
                            .background(SlightPurpleWhiteDarker),
                        onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=$address"))
                        context.startActivity(intent)
                    }) {
                        Icon(
                            Icons.Default.Directions,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            place.phoneNumber?.let { phoneNumber ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = basicPadding() / 2),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1.1f)
                    ) {
                        InfoRow(
                            label = stringResource(R.string.phone_number),
                            value = phoneNumber,
                            icon = Icons.Default.Phone
                        )
                    }

                    IconButton(
                        modifier = Modifier
                            .clip(RoundedCornerShape(basicCornerShape()))
                            .background(SlightPurpleWhiteDarker),
                        onClick = {
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                        context.startActivity(dialIntent)
                    }) {
                        Icon(
                            Icons.Default.PhoneInTalk,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = basicPadding() / 2),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    place.latLng?.let { latLng ->
                        InfoRow(
                            label = stringResource(R.string.latitude),
                            value = String.format("%.7f", latLng.latitude),
                            icon = Icons.Default.LocationOn
                        )
                    }
                    place.latLng?.let { latLng ->
                        InfoRow(
                            label = stringResource(R.string.longitude),
                            value = String.format("%.7f", latLng.longitude),
                            icon = Icons.Default.LocationOn
                        )
                    }
                    place.types?.firstOrNull()?.let {
                        InfoRow(
                            label = stringResource(R.string.type),
                            value = getLocalizedPlaceType(it, context),
                            icon = Icons.Default.HomeWork
                        )
                    }
                }

                place.openingHours?.let { openingHours ->
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = stringResource(R.string.opening_hours),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = basicPadding() / 2)
                            )

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = stringResource(R.string.opening_hours),
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(bottom = basicPadding() / 2)
                                )
                                openingHours.weekdayText.forEach { day ->
                                    Text(
                                        text = day,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(bottom = basicPadding() / 4)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(stringResource(R.string.close))
            }
        }
    }
}