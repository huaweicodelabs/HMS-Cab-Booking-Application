/*
 * Copyright 2021. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.huawei.hmscabsoftware.kotlin.view

import android.Manifest
import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.*
import android.text.TextUtils
import android.view.View
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.*
import com.huawei.hmf.tasks.OnSuccessListener
import com.huawei.hms.location.*
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.MapView
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.model.*
import com.huawei.hms.site.api.SearchResultListener
import com.huawei.hms.site.api.SearchService
import com.huawei.hms.site.api.SearchServiceFactory
import com.huawei.hms.site.api.model.*
import com.huawei.hms.site.widget.SearchIntent
import com.huawei.hmscabsoftware.R
import com.huawei.hmscabsoftware.kotlin.adapter.BookingTypeAdapter
import com.huawei.hmscabsoftware.kotlin.adapter.CabTypeAdapter
import com.huawei.hmscabsoftware.kotlin.adapter.RentalDataAdapter
import com.huawei.hmscabsoftware.kotlin.modal.*
import com.huawei.hmscabsoftware.kotlin.utils.*
import com.huawei.hmscabsoftware.kotlin.utils.ApiConstant.APIENCODEDKEY
import com.huawei.hmscabsoftware.kotlin.utils.ApiConstant.DRIVINGBASEURL
import com.huawei.hmscabsoftware.kotlin.utils.ApiConstant.DRIVINGSUFFIXURL
import com.huawei.hmscabsoftware.kotlin.utils.ApiConstant.REVERSEGEOCODESUFFIXURL
import com.huawei.hmscabsoftware.kotlin.utils.ApiConstant.SITEBASEURL
import com.huawei.hmscabsoftware.kotlin.utils.GlobalValues.HANDLER_THREE_SECOND
import com.huawei.hmscabsoftware.kotlin.utils.GlobalValues.RENTAL
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_cabbooking.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.collections.ArrayList
import kotlin.jvm.Throws

/**
 * HuaweiMapActivity class for implementing HMS kits like Map, Location and Site.
 * To use a map in your app, implement the OnMapReadyCallback API.
 */
class HuaweiMapActivity :
    AppCompatActivity(),
    OnMapReadyCallback,
    View.OnClickListener,
    BookingTypeAdapter.BookingTypeAdapterRecyclerViewItemClickListener,
    RentalDataAdapter.RentalRecyclerViewItemClickListener {

    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var settingsClient: SettingsClient
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocationCallback: LocationCallback

    private var searchIntent: SearchIntent? = null
    private var searchService: SearchService? = null
    private var huaweiMap: HuaweiMap? = null
    private var mMapView: MapView? = null

    private var originMarker: Marker? = null
    private var destinationMarker: Marker? = null
    private var movingCabMarker: Marker? = null
    private var grayPolyline: Polyline? = null
    private var blackPolyline: Polyline? = null
    private var previousLatLng: LatLng? = null
    private var carCurrentLatLng: LatLng? = null
    private lateinit var driverCurrentLatLng: LatLng
    private var userCurrentLatLng: LatLng? = null

    private var mDriverUserPathsList: ArrayList<LatLng> = ArrayList()

    private var cabTypeArrayList: ArrayList<CabTypeModal>? = null
    private var rentalTypeArrayList: ArrayList<RentalTypeModal> = ArrayList()
    private var nearByCabsArrayList: ArrayList<NearbyCabsModel>? = ArrayList()

    private var bookingTypeModel: BookingTypeModal? = null
    private var rentalDialog: RentalDialog? = null

    private val mUIHandler = Handler()
    private lateinit var mHandlerCarMovement: Handler
    private lateinit var mRunnableCarMovement: Runnable

    private var isSourceAddressField: Boolean = false
    private var isDestinationReached = false
    private var pickupLat: Double = 0.0
    private var pickupLng: Double = 0.0
    private var dropLat: Double = 0.0
    private var dropLng: Double = 0.0
    private val JSON = MediaType.parse("application/json; charset=utf-8")

    companion object {
        const val PERMISSIONS_REQUEST_CODE_ONE = 1
        const val PERMISSIONS_REQUEST_CODE_TWO = 2
        const val SEARCH_SOURCE_ADDRESS_REQUEST_CODE = 100
        const val SEARCH_DESTINATION_ADDRESS_REQUEST_CODE = 101
        const val RETURNPOI_VALUE = true
        const val HANDLER_ONE_SECOND : Long = 1000
        const val HANDLER_TWO_FIVE_SECOND : Long = 2500
        const val HANDLER_FIVE_SECOND : Long = 5000
        const val QUERY_VALUE = "India"
        const val RADIUS = 10000
        const val LANGUAGE_VALUE = "en"
        const val PAGEINDEX = 1
        const val PAGESIZE = 5
        const val POLITICALVIEW_VALUE = "IN"
        const val RETURN_CODE = "returnCode"
        const val SITES = "sites"
        const val FORMAT_ADDRESS = "formatAddress"
        const val LNG = "lng"
        const val LAT = "lat"
        const val LOCATION = "location"
        const val LANGUAGE = "language"
        const val POLITICALVIEW = "politicalView"
        const val ORIGIN = "origin"
        const val DESTINATION = "destination"
        const val ROUTES = "routes"
        const val PATHS = "paths"
        const val STEPS = "steps"
        const val POLYLINE = "polyline"
        const val ERRORMSG = "errorMsg"
        const val RETURNPOI = "returnPoi"
        const val BOOKINGTYPE = "BookingType"
        const val BOOKINGTYPEID = "BookingType_Id"
        const val CABTYPE = "Cabtype"
        const val CAB_ID = "cab_id"
        const val CAB_TYPE = "cab_type"
        const val CAB_SEATS = "cab_seats"
        const val BOOKING_TYPE = "booking_type"
        const val DRIVER_ID = "DriverId"
        const val DRIVER_NAME = "DriverName"
        const val CAR_NUMBER = "Car_Number"
        const val PHONE_NUMBER = "Phone_Number"
        const val RATING = "Rating"
        const val TOTAL_NO_OF_TRAVEL = "Total_no_of_travel"
        const val DRIVER_JSON_PATH = "DriverDetail.json"
        const val BOOKINGCABTYPE_JSON_PATH = "BookingCabType.json"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cabbooking)

        // Initialize the location update callback
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult != null) {
                    // Process the location callback result.
                    val location: Location = locationResult.locations[0]
                    // Define logic for processing the Location object upon success.
                    moveCamera(LatLng(location.latitude, location.longitude))
                    pickupLat = location.getLatitude()
                    pickupLng = location.getLongitude()
                    userCurrentLatLng = LatLng(location.getLatitude(), location.getLongitude())
                    // Calling Nearby Search Site API
                    getNearByCabsSite()
                    isSourceAddressField = true
                    // Calling Reverse Geocode Site API
                    reverseGeocoding(pickupLat, pickupLng)
                    // Stop requestlocationupdate callback, once retrieved current location
                    removeLocatonUpdates()
                }
            }
        }

        // Location service
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // Obtain a SettingsClient instance
        settingsClient = LocationServices.getSettingsClient(this)
        // Checking permission
        if (checkPermission()) {
            checkLocationSettings()
        } else {
            requestPermission()
        }

        // Add map instance
        mapView?.onCreate(savedInstanceState)
        mapView?.onResume()
        // getMapAsync() to register the callback
        mapView?.getMapAsync(this)

        // Initialize Search Service
        getLocationFromService()

        // Initliaze Onclicklistener
        pickUpLocation.setOnClickListener(this)
        dropLocation.setOnClickListener(this)
        confirmBooking.setOnClickListener(this)
        customCurrentLocation.setOnClickListener(this)

        // Fetching booking cab type arraylist from assets file
        parseBookingCabTypeJsonFromAssets()
    }

    /**
     * Checks the permissions
     * @return boolean value
     */
    fun checkPermission(): Boolean {
        val fineLocation =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocation =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fineLocation == PackageManager.PERMISSION_GRANTED &&
                coarseLocation == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Add runtime permissions for creating a location provider client in the activity.
     */
    private fun requestPermission() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val strings = arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                ActivityCompat.requestPermissions(this, strings, PERMISSIONS_REQUEST_CODE_ONE)
            }
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    this,
                    ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val strings = arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    ACCESS_BACKGROUND_LOCATION
                )
                ActivityCompat.requestPermissions(this, strings, PERMISSIONS_REQUEST_CODE_TWO)
            }
        }
    }

    /**
     * Use the onRequestPermissionsResult function to receive the permission verification result.
     * @param requestCode as int
     * @param permissions as String[]
     * @param grantResults as int[]
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        // Check whether requestCode is set to the value of PERMISSIONS_REQUEST_CODE_ONE, PERMISSIONS_REQUEST_CODE_TWO during permission application, and then check whether the permission is enabled.
        if (requestCode == PERMISSIONS_REQUEST_CODE_ONE || requestCode == PERMISSIONS_REQUEST_CODE_TWO) {
            if (grantResults.size > 0) {
                val fineLocation = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val coarseLocation = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (fineLocation
                    && coarseLocation
                ) {
                    checkLocationSettings()
                } else {
                    Toast.makeText(
                        this,
                        resources.getString(R.string.access_needed),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    /**
     * Creating location request.
     * @return LocationRequest
     */
    private fun createLocationRequest(): LocationRequest {
        mLocationRequest = LocationRequest()
        // Set the location update interval (int milliseconds).
        mLocationRequest.interval = HANDLER_ONE_SECOND
        // Set the weight.
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        return mLocationRequest
    }

    /**
     * Checking the device location settings.
     */
    private fun checkLocationSettings() {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(createLocationRequest())
        val locationSettingsRequest = builder.build()
        // Before requesting location update, invoke checkLocationSettings to check device settings.
        settingsClient.checkLocationSettings(locationSettingsRequest) // Define callback for success in checking the device location settings.
            .addOnSuccessListener {
                // Initiate location requests when the location settings meet the requirements.
                mFusedLocationProviderClient
                    .requestLocationUpdates(
                        mLocationRequest,
                        mLocationCallback,
                        Looper.getMainLooper()
                    ) // Define callback for success in requesting location updates.
                    .addOnSuccessListener { }
                    // Define callback for failure in requesting location updates.
                    .addOnCanceledListener {
                        getLastLocation()
                    }
            } // Define callback for failure in checking the device location settings.
            .addOnFailureListener {
                // Device location settings do not meet the requirements.
            }
    }

    /**
     * When requesting location updates is stopped, the mLocationCallback object must be the same as LocationCallback in the requestLocationUpdates method.
     */
    private fun removeLocatonUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback) // Define callback for success in stopping requesting location updates.
            .addOnSuccessListener { } // Define callback for failure in stopping requesting location updates.
            .addOnFailureListener { }
    }

    /**
     * Declare the SearchService object and use the SearchServiceFactory class to instantiate the object.
     * To create a SearchService instance, you need to pass the Context and API key parameters.
     * The API key is generated together with the agconnect-services.json file when you create an app in AppGallery Connect.
     * Note: the API key must be encoded using encodeURI.
     */
    private fun getLocationFromService() {
        searchService =
            SearchServiceFactory.create(
                this,
                APIENCODEDKEY
            )
    }

    /**
     * Add the life cycle method of the MapView.
     */
    override fun onStart() {
        super.onStart()
        mMapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mMapView?.onStop()
    }
    override fun onDestroy() {
        super.onDestroy()
        mMapView?.onDestroy()
    }

    override fun onPause() {
        mMapView?.onPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        mMapView?.onResume()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView?.onLowMemory()
    }

    /**
     * Call the onMapReady callback to obtain the HuaweiMap object.
     * @param map as HuaweiMap
     * Fetching the current location will be pointed on the huawei map.
     * Listener is set for camera moving events, the application layer can listen on the camera moving status.
     * When the camera starts to move, the onCameraMoveStarted() method of HuaweiMap.OnCameraMoveStartedListener will be called.
     */
    override fun onMapReady(map: HuaweiMap) {
        huaweiMap = map
        huaweiMap?.isMyLocationEnabled = true
        huaweiMap?.uiSettings?.isMyLocationButtonEnabled = false

        huaweiMap?.setOnCameraMoveStartedListener {
            when (it) {
                HuaweiMap.OnCameraMoveStartedListener.REASON_GESTURE -> {
                    if (customPickDestination.visibility == VISIBLE) {
                        val midLatLng: LatLng = huaweiMap?.cameraPosition!!.target
                        if (midLatLng != null) {
                            midLatLng.apply {
                                dropLat = latitude
                                dropLng = longitude
                            }
                            isSourceAddressField = false
                            // Reverse Geocode Site API
                            reverseGeocoding(dropLat, dropLng)
                        }
                    }
                }
                HuaweiMap.OnCameraMoveStartedListener
                    .REASON_API_ANIMATION -> {
                    // The user tapped something on the map.
                }
                HuaweiMap.OnCameraMoveStartedListener
                    .REASON_DEVELOPER_ANIMATION -> {
                    // The app moved the camera.
                }
            }
        }
    }

    /**
     * Fetching the current location will be pointed on the huawei map with the help of lastlocation on FusedLocationProviderClient.
     */
    private fun getLastLocation() {
        val lastLocation =
            mFusedLocationProviderClient.lastLocation
        lastLocation.addOnSuccessListener(
            OnSuccessListener { location ->
                if (location == null) {
                    return@OnSuccessListener
                }
                location.apply {
                    moveCamera(LatLng(latitude, longitude))
                    pickupLat = latitude
                    pickupLng = longitude
                    userCurrentLatLng = LatLng(latitude, longitude)
                    // Calling Nearby Search Site API
                    getNearByCabsSite()
                }
                isSourceAddressField = true
                // Calling Reverse Geocode Site API
                reverseGeocoding(pickupLat, pickupLng)
                return@OnSuccessListener
            }
        ).addOnFailureListener { e ->
        }
    }

    /**
     * Create a NearbySearchRequest object, which is used as the request body for nearby place search. In the request, the location parameter is mandatory and other parameters are optional.
     * Create a SearchResultListener object to listen for the search result.
     * Obtain the NearbySearchResponse object using the created SearchResultListener object. You can obtain a Site object from the NearbySearchResponse object and then parse it to obtain specific search results.
     */
    private fun getNearByCabsSite() {
        var nearByCabsModel: NearbyCabsModel?
        // Instantiate the SearchService object.
        searchService = SearchServiceFactory.create(this, APIENCODEDKEY)
        // Create a request body.
        val request = NearbySearchRequest()
        if (userCurrentLatLng == null) {
            return
        }
        val coordinate = Coordinate(userCurrentLatLng!!.latitude, userCurrentLatLng!!.longitude)
        request.setLocation(coordinate)
        request.setQuery(QUERY_VALUE)
        request.setRadius(RADIUS)
        request.setPoiType(LocationType.ADDRESS)
        request.setLanguage(LANGUAGE_VALUE)
        request.setPageIndex(PAGEINDEX)
        request.setPageSize(PAGESIZE)
        request.setPoliticalView(POLITICALVIEW_VALUE)
        // Create a search result listener.
        val resultListener: SearchResultListener<NearbySearchResponse?> =
            object : SearchResultListener<NearbySearchResponse?> {
                // Return search results upon a successful search.
                override fun onSearchResult(results: NearbySearchResponse?) {
                    if (results == null || results.getTotalCount() <= 0) {
                        return
                    }
                    val sites = results.sites
                    if (sites != null) {
                        for (site in sites) {
                            if (site == null) {
                                return
                            }
                            site.apply {
                                nearByCabsModel = NearbyCabsModel(
                                    siteId,
                                    location.lat.toString(),
                                    location.lng.toString()
                                )
                                nearByCabsArrayList?.add(nearByCabsModel!!)
                            }
                        }
                        nearByCabsArrayList?.let { it1 -> addNearByCabsMarker(it1) }
                    }
                }

                // Return the result code and description upon a search exception.
                override fun onSearchError(status: SearchStatus) {
                }
            }
        // Call the nearby place search API.
        searchService?.nearbySearch(request, resultListener)
    }

    /**
     * Parse the response body to obtain specific search results and pointed on the huawei map using marker.
     * @param responseBody as ArrayList<NearbyCabsModel>
     */
    private fun addNearByCabsMarker(responseBody: ArrayList<NearbyCabsModel>) {
        nearByCabsArrayList = responseBody
        for (item in responseBody) {
            if (item == null) {
                return
            }
            val latlng = LatLng(item.lat.toDouble(), item.lng.toDouble())
            val icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_white_car)
            huaweiMap?.addMarker(
                MarkerOptions().position(latlng).anchor(0.5f, 0.9f).icon(
                    icon
                )
                    .snippet(latlng.toString())
            )
        }
    }

    /**
     * Convert current coordinate into address with the help of reverseGeocode API.
     * @param lat latitude as Double value
     * @param lng longitude as Double value
     * Obtain the response from the reverseGeocode API. Parse it and get address value from json response with the help of formatAddress key and Render result into input field
     */
    private fun reverseGeocoding(lat: Double, lng: Double) {
        var address: String
        val jsonRequest = JSONObject()
        val location = JSONObject()
        try {
            location.put(LNG, lng)
            location.put(LAT, lat)
            jsonRequest.put(LOCATION, location)
            jsonRequest.put(LANGUAGE, LANGUAGE_VALUE)
            jsonRequest.put(POLITICALVIEW, POLITICALVIEW_VALUE)
            jsonRequest.put(RETURNPOI, RETURNPOI_VALUE)
        } catch (e: JSONException) {
        }
        val requestBody: RequestBody = RequestBody.create(JSON, jsonRequest.toString())
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(SITEBASEURL + REVERSEGEOCODESUFFIXURL + APIENCODEDKEY)
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(object : Callback {
            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body()!!.string()
                val jsonResponse = JSONObject(responseBody)
                val responseCode: String = jsonResponse.getString(RETURN_CODE)
                if (responseCode.contentEquals("0")) {
                    val jsonArraySites: JSONArray = jsonResponse.getJSONArray(SITES)
                    val jsonObjectDetail: JSONObject = jsonArraySites.getJSONObject(0)
                    address = jsonObjectDetail.getString(FORMAT_ADDRESS)
                    if (address != null) {
                        if (isSourceAddressField)
                            pickUpLocation?.text = address
                        else
                            dropLocation?.text = address
                    }
                    runOnUiThread {
                        // Stuff that updates the UI
                        if (pickupLat > 0 && dropLat > 0) {
                            confirmBooking.isEnabled = true
                        }
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
            }
        })
    }

    /**
     * Implementing onclick event
     * @param p0 as View
     */
    override fun onClick(p0: View?) {
        val id = p0?.id
        if (id == R.id.pickUpLocation) {
            locationBox(SEARCH_SOURCE_ADDRESS_REQUEST_CODE)
        } else if (id == R.id.dropLocation) {
            locationBox(SEARCH_DESTINATION_ADDRESS_REQUEST_CODE)
        } else if (id == R.id.confirmBooking) {
            if (!TextUtils.isEmpty(pickUpLocation?.text) && !TextUtils.isEmpty(dropLocation?.text)) {
                showConfirmDialog()
            } else
                toast(getString(R.string.error_message_source_destination_is_needed))
        } else if (id == R.id.customCurrentLocation) {
            getLastLocation()
        }
    }

    /**
     * Declare search intent. Its help to navigating screen to place search suggestion site sdk. By the help of android startActivityForResult() method, we can get result from another activity.
     * @param requestcode as Integer
     */
    private fun locationBox(requestcode: Int) {
        searchIntent = SearchIntent()
        searchIntent!!.setApiKey(APIENCODEDKEY)
        val intent = searchIntent!!.getIntent(this)
        startActivityForResult(intent, requestcode)
    }

    /**
     * Callback method  to get the message form other Activity
     * @param requestCode as Integer
     * @param requestCode as Integer
     * @param data as Intent
     */
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        @Nullable data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SEARCH_SOURCE_ADDRESS_REQUEST_CODE) {
            if (SearchIntent.isSuccess(resultCode)) {
                isSourceAddressField = true
                val site: Site = searchIntent!!.getSiteFromIntent(data)
                if (site != null) {
                    pickUpLocation?.text = site.name
                    querySuggestion()
                }
            }
        }
        if (requestCode == SEARCH_DESTINATION_ADDRESS_REQUEST_CODE) {
            if (SearchIntent.isSuccess(resultCode)) {
                isSourceAddressField = false
                val site: Site = searchIntent!!.getSiteFromIntent(data)
                if (site != null) {
                    dropLocation?.text = site.name
                    querySuggestion()
                }
            }
        }
    }

    /**
     * Create a QuerySuggestionRequest object, which is used as the request body for search suggestion. Related parameters are as follows, among which query is mandatory and others are optional.
     * query: search keyword
     * location: longitude and latitude to which search results need to be biased
     * radius: search radius, in meters. The value ranges from 1 to 50000. The default value is 50000
     * poiTypes: list of POI types like GEOCODE, ADDRESS, ESTABLISHMENT, REGIONS, CITIES
     * Pass the created QuerySuggestionRequest object.
     */
    private fun querySuggestion() {
        val request = QuerySuggestionRequest()
        val query: String? = if (isSourceAddressField) {
            pickUpLocation?.text.toString()
        } else {
            dropLocation?.text.toString()
        }
        if (!TextUtils.isEmpty(query)) {
            request.query = query
        }
        searchService?.querySuggestion(
            request,
            searchResultListener as SearchResultListener<QuerySuggestionResponse>?
        )
    }

    /**
     * Create a SearchResultListener object to listen for the search result.
     * Obtain the search result from onSearchResult, and display places in the search result.
     */
    private var searchResultListener =
        object : SearchResultListener<QuerySuggestionResponse?> {
            // Return search results upon a successful search.
            override fun onSearchResult(results: QuerySuggestionResponse?) {
                val stringBuilder = StringBuilder()
                results?.let {
                    val sites = results.sites
                    if (sites != null && sites.size > 0) {
                        for (site in sites) {
                            if (site == null) {
                                return
                            }
                            val location = site.location
                            with(location) {
                                if (isSourceAddressField) {
                                    pickupLat = lat
                                    pickupLng = lng
                                    if (dropLat> 0) {
                                        driverCurrentLatLng = LatLng(dropLat, dropLng)
                                        userCurrentLatLng = LatLng(pickupLat, pickupLng)
                                        confirmBooking.isEnabled = true
                                        moveCamera(userCurrentLatLng!!)
                                    }
                                } else {
                                    dropLat = lat
                                    dropLng = lng
                                    if (pickupLat> 0) {
                                        driverCurrentLatLng = LatLng(dropLat, dropLng)
                                        userCurrentLatLng = LatLng(pickupLat, pickupLng)
                                        confirmBooking.isEnabled = true
                                        moveCamera(driverCurrentLatLng)
                                    }
                                }
                            }
                            break
                        }
                    } else {
                        stringBuilder.append(getString(R.string.no_result))
                    }
                }
            }
            // Return the result code and description upon a search exception.
            override fun onSearchError(status: SearchStatus) {
            }
        }

    /**
     * Alert dialog can be used to interrupt and ask the user choice to continue or discontinue.
     * Click NO to Ignore and CONFIRM to confirm the booking.
     * parseDriverDetailJsonFromAssets() method is used to fetching driver details from the asset json file.
     */
    private fun showConfirmDialog() {
        val builder =
            AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.dialog_booking_title))
            .setCancelable(false)
            .setPositiveButton(
                getString(R.string.confirm)
            ) { dialog, which ->
                huaweiMap?.uiSettings?.isScrollGesturesEnabled = false
                progress_bar.show()

                tvOtp.text = getString(R.string.otp)

                // Fetching driver details from the asset json file
                parseDriverDetailJsonFromAssets()

                mUIHandler.postDelayed(
                    {
                        progress_bar.hide()
                        huaweiMap?.uiSettings?.isScrollGesturesEnabled = true
                        confirmCabBooking()
                    },
                    HANDLER_ONE_SECOND
                )
            }
            .setNegativeButton(
                getString(R.string.no)
            ) { dialog, which ->
                huaweiMap?.uiSettings?.isScrollGesturesEnabled = true
                dialog.dismiss()
            }
        // Creating dialog box
        val dialog = builder.create()
        dialog.show()
    }

    /**
     * Handle the UI once confirm booking.
     * Calling direction API for draw the route between source and destination.
     */
    private fun confirmCabBooking() {
        layout_bookingdetails.visibility = VISIBLE
        layout_booking.visibility = View.GONE
        customPickDestination.visibility = View.GONE
        pickUpLocation?.isEnabled = false
        dropLocation?.isEnabled = false
        if (nearByCabsArrayList?.size!! > 0) {
            val lat: Double = nearByCabsArrayList?.get(0)?.lat!!.toDouble()
            val lng: Double = nearByCabsArrayList?.get(0)?.lng!!.toDouble()
            driverCurrentLatLng = LatLng(lat, lng)
        } else {
            toast(getString(R.string.error_message_cab_not_available_your_location))
            return
        }
        userCurrentLatLng = LatLng(pickupLat, pickupLng)
        directionAPI(driverCurrentLatLng, userCurrentLatLng!!)
    }

    /**
     * Implementation of Direction API with the help of Start and End Location.
     * The driving route planning API can:
     * Return up to 3 routes for a request.
     * Support up to 5 waypoints.
     * Support planning for travel in the future.
     * Plan routes based on real-time traffic conditions.
     * @param startLoc as LatLng
     * @param endLoc as LatLng
     */
    private fun directionAPI(startLoc: LatLng, endLoc: LatLng) {
        huaweiMap?.clear()
        val jsonRequest = JSONObject()
        val origin = JSONObject()
        val destination = JSONObject()
        if (startLoc == null || endLoc == null) {
            return
        }
        try {
            origin.put(LAT, startLoc.latitude)
            origin.put(LNG, startLoc.longitude)
            destination.put(LAT, endLoc.latitude)
            destination.put(LNG, endLoc.longitude)
            jsonRequest.put(ORIGIN, origin)
            jsonRequest.put(DESTINATION, destination)
        } catch (e: JSONException) {
        }
        val requestBody = RequestBody.create(JSON, jsonRequest.toString())
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(DRIVINGBASEURL + DRIVINGSUFFIXURL + APIENCODEDKEY)
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException) {
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call?, response: Response) {
                val responseBody = response.body()!!.string()
                if (responseBody != null) {
                    generateRouteForMap(responseBody)
                }
            }
        })
    }

    /**
     * Obtain the response from the direction API. Parse it and draw route using polyline.
     * @param routes contains responsebody from the direction API as string
     */
    private fun generateRouteForMap(routes: String) {
        mDriverUserPathsList = ArrayList()
        try {
            val jsonResponse = JSONObject(routes)
            val routesList = jsonResponse.getJSONArray(ROUTES)
            if (routesList.length() == 0) {
                return
            }
            val route = routesList.getJSONObject(0) ?: return
            val paths = route.getJSONArray(PATHS)
            if (paths.length() == 0) {
                return
            }
            for (i in 0 until paths.length()) {
                val path = paths.getJSONObject(i)
                val steps = path.getJSONArray(STEPS)
                for (j in 0 until steps.length()) {
                    val step = steps.getJSONObject(j)
                    val polyline = step.getJSONArray(POLYLINE)
                    for (k in 0 until polyline.length()) {
                        if (j > 0 && k == 0) {
                            continue
                        }
                        val line = polyline.getJSONObject(k)
                        if (line != null) {
                            val lat = line.getDouble(LAT)
                            val lng = line.getDouble(LNG)
                            val latLng = LatLng(lat, lng)
                            mDriverUserPathsList.add(latLng)
                        }
                    }
                }
            }
            mHandler.sendEmptyMessage(0)
        } catch (e: JSONException) {
        }
    }

    /**
     * Rendering the result on Huawei Map using Handler.
     */
    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0 -> driverComingToYourLocation()
                1 -> {
                    val bundle = msg.data
                    val errorMsg = bundle.getString(ERRORMSG)
                    toast(errorMsg!!)
                }
            }
        }
    }

    /**
     * Showing path from driver location to customer location using polyline and move cab marker as well
     */
    private fun driverComingToYourLocation() {
        if (!isDestinationReached) {
            this.sendNotification(
                getString(R.string.notification_message_body) + "${tvOtp.text}",
                getString(R.string.notification_message_ride_on_the_way)
            )
            showDefaultLocationOnMap(driverCurrentLatLng)
        } else {
            userCurrentLatLng?.let { showDefaultLocationOnMap(it) }
        }

        Handler().postDelayed(
            {
                showPath(mDriverUserPathsList)
                showMovingCab(mDriverUserPathsList)
            },
            HANDLER_THREE_SECOND
        )
    }

    /**
     * Call move and animate camera method
     * @param latLng as LatLng
     */
    private fun showDefaultLocationOnMap(latLng: LatLng) {
        if (latLng == null) {
            return
        }
        moveCamera(latLng)
        animateCamera(latLng)
    }

    /**
     * Move the map camera in non-animation mode.
     * @param latLng as LatLng
     */
    private fun moveCamera(latLng: LatLng) {
        if (latLng == null) {
            return
        }
        huaweiMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    /**
     * Move the map camera in animation mode.
     * @param latLng as LatLng
     */
    private fun animateCamera(latLng: LatLng) {
        if (latLng == null) {
            return
        }
        val cameraPosition = CameraPosition.Builder().target(latLng).zoom(17f).build()
        huaweiMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    /**
     * Rendering the route on huawei map using polyline
     * @param latLngList as ArrayList<LatLng>
     */
    private fun showPath(latLngList: ArrayList<LatLng>) {
        val builder = LatLngBounds.Builder()
        if (latLngList == null) {
            return
        }
        for (latLng in latLngList) {
            builder.include(latLng)
        }
        val bounds = builder.build()
        huaweiMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 2))

        val polylineOptions = PolylineOptions()
        polylineOptions.apply {
            color(Color.GRAY)
            width(5f)
            addAll(latLngList)
            grayPolyline = huaweiMap?.addPolyline(polylineOptions)
        }

        val blackPolylineOptions = PolylineOptions()
        blackPolylineOptions.apply {
            color(Color.BLACK)
            width(5f)
            blackPolyline = huaweiMap?.addPolyline(blackPolylineOptions)
        }

        originMarker = addOriginDestinationMarker(latLngList[0])
        originMarker?.setAnchor(0.5f, 0.5f)
        destinationMarker = addOriginDestinationMarker(latLngList[latLngList.size - 1])
        destinationMarker?.setAnchor(0.5f, 0.5f)

        val polylineAnimator = AnimationUtils.polylineAnimator()
        polylineAnimator.addUpdateListener { valueAnimator ->
            val percentValue = (valueAnimator.animatedValue as Int)
            val index = (grayPolyline?.points!!.size) * (percentValue / 100.0f).toInt()
            blackPolyline?.points = grayPolyline?.points!!.subList(0, index)
        }
        polylineAnimator.start()
    }

    /**
     * Pointing marker on huawei map using source and destination coordinate with the help of marker
     * @param latLng as Marker
     * @return Marker
     */
    private fun addOriginDestinationMarker(latLng: LatLng): Marker? {
        val bitmapDescriptor =
            BitmapDescriptorFactory.fromBitmap(MapUtils.getOriginDestinationMarkerBitmap())
        return huaweiMap?.addMarker(
            MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
        )
    }

    /**
     * Automatically move the cab from source to destination with response array and render it on huawei map using handler with duration.
     * @param cabLatLngList as ArrayList<LatLng>
     */
    private fun showMovingCab(cabLatLngList: ArrayList<LatLng>) {
        mHandlerCarMovement = Handler()
        var index = 0
        mRunnableCarMovement = Runnable {
            run {
                if (index < mDriverUserPathsList.size - 1) {
                    updateCarLocation(cabLatLngList[index])
                    mHandlerCarMovement.postDelayed(mRunnableCarMovement, HANDLER_THREE_SECOND)
                    ++index
                } else {
                    mHandlerCarMovement.removeCallbacks(mRunnableCarMovement)
                    if (!isDestinationReached) {
                        toast(getString(R.string.your_ride_is_here))
                        this.sendNotification(
                            getString(R.string.notification_message_body) + "${tvOtp.text}",
                            getString(R.string.your_ride_is_here)
                        )
                        travelWithCustomer()
                    } else {
                        toast(getString(R.string.thanks_message))
                        this.sendNotification(
                            getString(R.string.destination_reached_message),
                            getString(R.string.thanks_title)
                        )
                        progress_bar.show()
                        mUIHandler.postDelayed(
                            {
                                progress_bar.hide()
                                clearMapView()
                            },
                            HANDLER_TWO_FIVE_SECOND
                        )
                    }
                }
            }
        }
        mHandlerCarMovement.postDelayed(mRunnableCarMovement, HANDLER_FIVE_SECOND)
    }

    /**
     * Calling again direction api when driver reached customer place for pickup.
     */
    private fun travelWithCustomer() {
        isDestinationReached = true
        // Waiting for customer
        progress_bar.show()
        mUIHandler.postDelayed(
            {
                progress_bar.hide()
                movingCabMarker = null
                previousLatLng = null
                driverCurrentLatLng = LatLng(dropLat, dropLng)
                userCurrentLatLng = LatLng(pickupLat, pickupLng)
                if (driverCurrentLatLng != null && userCurrentLatLng != null) {
                    directionAPI(userCurrentLatLng!!, driverCurrentLatLng)
                }
            },
            HANDLER_FIVE_SECOND
        )
    }

    /**
     * When cab is on moving, update the cab marker on huawei map with animation using current location.
     * @param latLng as LatLng
     */
    private fun updateCarLocation(latLng: LatLng) {
        if (latLng == null) {
            return
        }
        if (movingCabMarker == null) {
            movingCabMarker = addCarMarker(latLng)
        }
        if (previousLatLng == null) {
            carCurrentLatLng = latLng
            previousLatLng = carCurrentLatLng
            movingCabMarker?.position = carCurrentLatLng
            movingCabMarker?.setAnchor(0.5f, 0.5f)
            animateCamera(carCurrentLatLng!!)
        } else {
            previousLatLng = carCurrentLatLng
            carCurrentLatLng = latLng
            val valueAnimator = AnimationUtils.carAnimator()
            valueAnimator.addUpdateListener { va ->
                if (carCurrentLatLng != null && previousLatLng != null) {
                    val multiplier = va.animatedFraction
                    val nextLocation = LatLng(
                        multiplier * carCurrentLatLng!!.latitude + (1 - multiplier) * previousLatLng!!.latitude,
                        multiplier * carCurrentLatLng!!.longitude + (1 - multiplier) * previousLatLng!!.longitude
                    )
                    movingCabMarker?.position = nextLocation
                    val rotation = MapUtils.getRotation(previousLatLng!!, nextLocation)
                    if (!rotation.isNaN()) {
                        movingCabMarker?.rotation = rotation
                    }
                    movingCabMarker?.setAnchor(0.5f, 0.5f)
                    animateCamera(nextLocation)
                }
            }
            valueAnimator.start()
        }
    }

    /**
     * Pointing cab marker on huawei map using latlng
     * @param latLng as LatLng
     * @return Marker
     */
    private fun addCarMarker(latLng: LatLng): Marker? {
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(
            MapUtils.getCabBitmap(
                this
            )
        )
        return huaweiMap?.addMarker(
            MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
        )
    }

    /**
     * Select the booking type and update it on recyclerview using adapter
     * @param bookingType as BookingTypeModal
     */
    override fun clickOnItem(bookingType: BookingTypeModal) {
        bookingTypeModel = bookingType
        bookingType.apply {
            if (bookingTypeName.contentEquals(RENTAL)) {
                openRentalDialog()
            }
            cabTypeArrayList = cabTypeModalList
            updateCabTypeAdapter(cabTypeArrayList)
        }
    }

    /**
     * Showing rental dialog when user select rental booking type
     */
    private fun openRentalDialog() {
        rentalTypeArrayList = ArrayList()
        val rent1 = RentalTypeModal(getString(R.string.one_hour_ten_kms))
        val rent2 = RentalTypeModal(getString(R.string.two_hour_twenry_kms))
        val rent3 = RentalTypeModal(getString(R.string.four_hour_fouty_kms))
        val rent4 = RentalTypeModal(getString(R.string.six_hour_sixty_kms))
        val rent5 = RentalTypeModal(getString(R.string.eight_hours_eighty_kms))
        val rent6 = RentalTypeModal(getString(R.string.ten_hours_hundred_kms))
        rentalTypeArrayList.add(rent1)
        rentalTypeArrayList.add(rent2)
        rentalTypeArrayList.add(rent3)
        rentalTypeArrayList.add(rent4)
        rentalTypeArrayList.add(rent5)
        rentalTypeArrayList.add(rent6)
        val dataAdapter = RentalDataAdapter(rentalTypeArrayList, this)
        rentalDialog = RentalDialog(this, dataAdapter)
        // if we know that the particular variable not null any time ,we can assign !! (not null operator ), then  it won't check for null, if it becomes null, it willthrow exception
        rentalDialog!!.show()
        rentalDialog!!.setCanceledOnTouchOutside(false)
    }

    /**
     * Callback method for close the rental dialog once user selected any item on list which shown on dialog.
     * @param data as String
     */
    override fun clickOnRentalItem(data: String) {
        rentalDialog!!.dismiss()
    }

    /**
     * Fetching booking cab type arraylist from assets json file name "BookingCabType.json".
     * Parse it and construct array as bookingCabTypeResponse.
     */
    private fun parseBookingCabTypeJsonFromAssets() {
        val bookingCabTypeResponse: ArrayList<BookingTypeModal> = arrayListOf()
        try {
            // Extension Function call here
            val bookingCabTypeArray = JSONArray(loadJSONFromAssets(BOOKINGCABTYPE_JSON_PATH))
            for (i in 0 until bookingCabTypeArray.length()) {
                val bookingType = BookingTypeModal()
                val bookingCabTypeObject = bookingCabTypeArray.getJSONObject(i)
                bookingType.bookingTypeName = bookingCabTypeObject.getString(BOOKINGTYPE)
                bookingType.bookingTypeId = bookingCabTypeObject.getString(BOOKINGTYPEID)
                val cabTypeJsonArray = bookingCabTypeObject.getJSONArray(CABTYPE)
                for (j in 0 until cabTypeJsonArray.length()) {
                    val cabTypeModal = CabTypeModal()
                    val cabTypeJsonObject = cabTypeJsonArray.getJSONObject(j)
                    cabTypeModal.cabID = cabTypeJsonObject.getString(CAB_ID).toInt()
                    cabTypeModal.cabTypeName = cabTypeJsonObject.getString(CAB_TYPE)
                    cabTypeModal.cabSeats = cabTypeJsonObject.getString(CAB_SEATS)
                    cabTypeModal.bookingTypeName = cabTypeJsonObject.getString(BOOKING_TYPE)
                    bookingType.cabTypeModalList.add(cabTypeModal)
                }
                bookingCabTypeResponse.add(bookingType)
            }
        } catch (e: JSONException) {
        }

        updateBookingTypeAdapter(bookingCabTypeResponse)
    }

    /**
     * Parse it and render array result on cab type recyclerview in HORIZONTAL View.
     * @param bookingTypeResponse as ArrayList<BookingTypeModal>
     */
    private fun updateBookingTypeAdapter(bookingTypeResponse: ArrayList<BookingTypeModal>) {
        bookingRecylerView.layoutManager = LinearLayoutManager(
            this,
            HORIZONTAL,
            false
        )
        bookingRecylerView.adapter = BookingTypeAdapter(bookingTypeResponse, this)
        cabTypeArrayList = bookingTypeResponse[0].cabTypeModalList
        updateCabTypeAdapter(cabTypeArrayList)
    }

    /**
     * Updating cab type recyclerview using adapter.
     * @param cabType as ArrayList<CabTypeModal>
     */
    private fun updateCabTypeAdapter(cabType: ArrayList<CabTypeModal>?) {
        cabTypeRecylerView.layoutManager = LinearLayoutManager(
            this,
            HORIZONTAL,
            false
        )
        cabTypeRecylerView.adapter = cabType?.let { CabTypeAdapter(it) }
    }

    /**
     * Fetching driver details object from assets json file name "DriverDetail.json".
     */
    private fun parseDriverDetailJsonFromAssets() {
        val driverDetailResponse = DriverDetailsModel()
        try {
            // Extension Function call here
            val driverDetailJsonResponse = JSONObject(loadJSONFromAssets(DRIVER_JSON_PATH))
            driverDetailResponse.driverId = driverDetailJsonResponse.getString(DRIVER_ID)
            driverDetailResponse.driverName = driverDetailJsonResponse.getString(DRIVER_NAME)
            driverDetailResponse.carNumber = driverDetailJsonResponse.getString(CAR_NUMBER)
            driverDetailResponse.phoneNumber = driverDetailJsonResponse.getString(PHONE_NUMBER)
            driverDetailResponse.rating = driverDetailJsonResponse.getString(RATING)
            driverDetailResponse.totalNoOfTravel =
                driverDetailJsonResponse.getString(TOTAL_NO_OF_TRAVEL)
            onUpdateDriverDetails(driverDetailResponse)
        } catch (e: JSONException) {
        }
    }

    /**
     * Parse it and rendering driver details on UI once user confirm the booking.
     * @param driverResponse as DriverDetailsModel
     */
    private fun onUpdateDriverDetails(driverResponse: DriverDetailsModel) {
        with(driverResponse) {
            displayDriverAvatar()
            tvCabNumber.text = carNumber
            ratingBar.rating = rating.toFloat()
            if (bookingTypeModel != null) {
                tvBookingType.text = bookingTypeModel?.bookingTypeName
            }
            tvModel.text = getString(R.string.Car_Name)
            tvName.text = driverName
        }
    }

    /**
     * Rendering driver photo on ImageView using Picasso library.
     */
    private fun displayDriverAvatar() {
        Picasso.get()
            .load(R.drawable.ic_dummy_user)
            .into(img_driver)
    }

    /**
     * Reseting the UI.
     */
    private fun clearMapView() {
        huaweiMap?.clear()
        movingCabMarker = null
        previousLatLng = null
        isDestinationReached = false
        layout_bookingdetails.visibility = View.GONE
        layout_booking.visibility = VISIBLE
        pickUpLocation?.isEnabled = true
        dropLocation?.isEnabled = true
        huaweiMap?.uiSettings?.isScrollGesturesEnabled = true
        customPickDestination.visibility = VISIBLE
        parseBookingCabTypeJsonFromAssets()
        getLastLocation()
    }
}
