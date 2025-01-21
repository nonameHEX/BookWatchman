package cz.mendelu.bookwatchman.ui.screens.map

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.rememberCameraPositionState
import cz.mendelu.bookwatchman.BuildConfig
import cz.mendelu.bookwatchman.R
import cz.mendelu.bookwatchman.navigation.INavigationRouter
import cz.mendelu.bookwatchman.ui.elements.BaseScreen
import cz.mendelu.bookwatchman.ui.elements.MapBottomSheet
import cz.mendelu.bookwatchman.ui.theme.basicCornerShape
import cz.mendelu.bookwatchman.ui.theme.basicPadding

@Composable
fun MapScreen(
    navigation: INavigationRouter
){
    val viewModel = hiltViewModel<MapViewModel>()

    val places = remember { mutableStateOf<PlacesResponse?>(null) }

    val state = viewModel.uiState.collectAsStateWithLifecycle()

    state.value.let {
        when(it){
            MapUIState.Loading -> {}
            is MapUIState.DataLoaded -> {}
            is MapUIState.Error -> {}
        }
    }

    val userLocation = remember { mutableStateOf<LatLng?>(null) }
    val permissionGranted = remember { mutableStateOf(false) }

    val context = LocalContext.current
    RequestLocationPermission(
        onPermissionGranted = {
            permissionGranted.value = true
        },
        onPermissionDenied = {
            navigation.returnBack()
        }
    )

    val placesClient = remember {
        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(context, BuildConfig.API_KEY)
        }
        Places.createClient(context)
    }

    if (permissionGranted.value) {
        GetUserLocation(
            onLocationFound = { location ->
                userLocation.value = location

                fetchNearbyLibrariesAndBookstores(location, placesClient) { response ->
                    places.value = response
                }

                viewModel.updateToLoaded()
            },
            onLocationError = {
                Toast.makeText(context,
                    context.getString(R.string.unable_to_get_current_location), Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (permissionGranted.value && userLocation.value != null) {
        BaseScreen(
            showLoading = state.value is MapUIState.Loading,
            topBarText = stringResource(R.string.bookstores_in_area),
            onBackClick = {
                navigation.returnBack()
            }
        ) {
            MapScreenContent(
                paddingValues = it,
                userLocation = userLocation.value,
                places = places.value
            )
        }
    }
}

@SuppressLint("PotentialBehaviorOverride")
@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun MapScreenContent(
    paddingValues: PaddingValues,
    userLocation: LatLng?,
    places: PlacesResponse?
){
    if (places != null) {
        for (place in places.places){
            Log.d("MapScreenContent", "places count: ${place.name}")
        }
    }
    val mapUiSettings by remember { mutableStateOf(
        MapUiSettings(
            zoomControlsEnabled = false,
            mapToolbarEnabled = false)
    ) }
    val cameraPositionState = rememberCameraPositionState {
        userLocation?.let {
            position = CameraPosition.fromLatLngZoom(it, 15f)
        } ?: run {
            position = CameraPosition.fromLatLngZoom(LatLng(49.211230, 16.619347), 9f)
        }
    }

    var googleMap by remember {
        mutableStateOf<GoogleMap?>(null)
    }

    var selectedPlace by remember { mutableStateOf<PlacesResponse.Place?>(null) }

    val mapStyle = """
        [
            {
                "featureType": "poi",
                "elementType": "all",
                "stylers": [
                    {
                        "visibility": "off"
                    }
                ]
            }
        ]
    """.trimIndent()

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .clip(
            RoundedCornerShape(
                topStart = basicCornerShape() * 3,
                topEnd = basicCornerShape() * 3
            )
        )
    ) {
        GoogleMap(modifier = Modifier.fillMaxHeight(),
            uiSettings = mapUiSettings,
            cameraPositionState = cameraPositionState
        ){
            MapEffect { map ->
                if (googleMap == null){
                    googleMap = map

                    try {
                        googleMap?.setMapStyle(MapStyleOptions(mapStyle))
                    } catch (e: Exception) {
                        Log.e("MapScreen", "Style parsing failed: ", e)
                    }
                }
            }

            places?.places?.forEach { place ->
                place.latLng?.let { latLng ->
                    val markerOptions = MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                        .title(place.name)
                    val marker = googleMap?.addMarker(markerOptions)
                    marker?.tag = place.name
                }
            }

            userLocation?.let {
                googleMap?.addMarker(
                    MarkerOptions()
                        .position(it)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                )
            }

            googleMap?.setOnMarkerClickListener { marker ->
                val place = places?.places?.find { it.name == marker.tag }
                if (place != null) {
                    selectedPlace = place
                }
                true
            }
        }

        IconButton(
            onClick = {
                userLocation?.let {
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 14f))
                }
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(basicPadding())
                .clip(RoundedCornerShape(basicCornerShape()))
                .background(Color.White),
        ) {
            Icon(
                imageVector = Icons.Filled.MyLocation,
                contentDescription = stringResource(R.string.locate_me),
                tint = Color.Red
            )
        }

        selectedPlace?.let { place ->
            MapBottomSheet(
                place = place,
                onDismiss = { selectedPlace = null }
            )
        }
    }
}

@Composable
fun RequestLocationPermission(
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
                    context.getString(R.string.location_permission_denied_please_enable_it_in_the_app_settings),
                    Toast.LENGTH_SHORT
                ).show()
                onPermissionDenied()
            }
        }
    )

    LaunchedEffect(Unit) {
        permissionRequest.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }
}

@SuppressLint("MissingPermission")
@Composable
fun GetUserLocation(
    onLocationFound: (LatLng) -> Unit,
    onLocationError: () -> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val locationTask: Task<Location> = fusedLocationClient.lastLocation

    LaunchedEffect(Unit) {
        locationTask.addOnSuccessListener { location ->
            if (location != null) {
                onLocationFound(LatLng(location.latitude, location.longitude))
            } else {
                onLocationError()
            }
        }
    }
}

@SuppressLint("MissingPermission")
fun fetchNearbyLibrariesAndBookstores(
    userLocation: LatLng,
    placesClient: PlacesClient,
    onPlacesFetched: (PlacesResponse) -> Unit
) {
    val location = LatLng(userLocation.latitude, userLocation.longitude)
    val placeFields = listOf(
        Place.Field.ID,
        Place.Field.NAME,
        Place.Field.LAT_LNG,
        Place.Field.ADDRESS,
        Place.Field.TYPES,
        Place.Field.PHONE_NUMBER,
        Place.Field.WEBSITE_URI,
        Place.Field.RATING,
        Place.Field.OPENING_HOURS
    )
    val circle = CircularBounds.newInstance(location, 2000.0);
    val includedTypes: List<String> = mutableListOf("library", "book_store")

    val request = SearchNearbyRequest.builder(circle, placeFields)
        .setIncludedTypes(includedTypes)
        .setMaxResultCount(15)
        .build()

    val task = placesClient.searchNearby(request)

    task.addOnSuccessListener { taskResult ->
        val placesList = taskResult.places.map { place ->
            PlacesResponse.Place(
                name = place.name,
                latLng = place.latLng,
                address = place.address,
                id = place.id,
                types = place.types,
                phoneNumber = place.phoneNumber,
                websiteUri = place.websiteUri?.toString(),
                rating = place.rating,
                openingHours = place.openingHours
            )
        }

        val placesResponse = PlacesResponse(placesList)
        onPlacesFetched(placesResponse)
    }
    task.addOnFailureListener {
        Log.e("MapViewModel", "Error fetching nearby places", it)
    }
}
