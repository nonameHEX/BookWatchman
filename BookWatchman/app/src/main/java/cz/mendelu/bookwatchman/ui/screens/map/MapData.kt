package cz.mendelu.bookwatchman.ui.screens.map

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.OpeningHours
import com.google.android.libraries.places.api.model.Place.Type
import cz.mendelu.bookwatchman.R

data class PlacesResponse(
    val places: List<Place>
) {
    data class Place(
        val name: String?,
        val latLng: LatLng?,
        val address: String?,
        val id: String?,
        val types: List<Type>?,
        val phoneNumber: String?,
        val websiteUri: String?,
        val rating: Double?,
        val openingHours: OpeningHours?
    )
}

fun getLocalizedPlaceType(type: Type, context: Context): String {
    return when (type) {
        Type.LIBRARY -> context.getString(R.string.library)
        Type.BOOK_STORE -> context.getString(R.string.book_store)
        else -> type.toString()
    }
}