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
package com.huawei.hmscabsoftware.java.view;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hmf.tasks.OnCanceledListener;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.LocationSettingsResponse;
import com.huawei.hms.location.SettingsClient;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.model.BitmapDescriptor;
import com.huawei.hms.maps.model.BitmapDescriptorFactory;
import com.huawei.hms.maps.model.CameraPosition;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.LatLngBounds;
import com.huawei.hms.maps.model.Marker;
import com.huawei.hms.maps.model.MarkerOptions;
import com.huawei.hms.maps.model.Polyline;
import com.huawei.hms.maps.model.PolylineOptions;
import com.huawei.hms.site.api.SearchResultListener;
import com.huawei.hms.site.api.SearchService;
import com.huawei.hms.site.api.SearchServiceFactory;
import com.huawei.hms.site.api.model.Coordinate;
import com.huawei.hms.site.api.model.LocationType;
import com.huawei.hms.site.api.model.NearbySearchRequest;
import com.huawei.hms.site.api.model.NearbySearchResponse;
import com.huawei.hms.site.api.model.QuerySuggestionRequest;
import com.huawei.hms.site.api.model.QuerySuggestionResponse;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;
import com.huawei.hms.site.widget.SearchIntent;
import com.huawei.hmscabsoftware.R;
import com.huawei.hmscabsoftware.java.adapter.BookingTypeAdapter;
import com.huawei.hmscabsoftware.java.adapter.CabTypeAdapter;
import com.huawei.hmscabsoftware.java.adapter.RentalDataAdapter;
import com.huawei.hmscabsoftware.java.modal.BookingTypeModal;
import com.huawei.hmscabsoftware.java.modal.CabTypeModal;
import com.huawei.hmscabsoftware.java.modal.DriverDetailsModel;
import com.huawei.hmscabsoftware.java.modal.NearbyCabsModel;
import com.huawei.hmscabsoftware.java.modal.RentalTypeModal;
import com.huawei.hmscabsoftware.java.utils.AnimationUtils;
import com.huawei.hmscabsoftware.java.utils.MapUtils;
import com.huawei.hmscabsoftware.java.utils.RentalDialog;
import com.huawei.hmscabsoftware.java.utils.ViewUtils;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.Manifest.permission.ACCESS_BACKGROUND_LOCATION;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static androidx.recyclerview.widget.RecyclerView.HORIZONTAL;
import static com.huawei.hmscabsoftware.java.utils.ApiConstant.APIENCODEDKEY;
import static com.huawei.hmscabsoftware.java.utils.ApiConstant.DRIVINGBASEURL;
import static com.huawei.hmscabsoftware.java.utils.ApiConstant.DRIVINGSUFFIXURL;
import static com.huawei.hmscabsoftware.java.utils.ApiConstant.REVERSEGEOCODESUFFIXURL;
import static com.huawei.hmscabsoftware.java.utils.ApiConstant.SITEBASEURL;
import static com.huawei.hmscabsoftware.java.utils.GlobalValues.HANDLER_THREE_SECOND;
import static com.huawei.hmscabsoftware.java.utils.GlobalValues.RENTAL;

/**
 * HuaweiMapActivity class for implementing HMS kits like Map, Location and Site.
 * To use a map in your app, implement the OnMapReadyCallback API.
 */
public class HuaweiMapActivity extends AppCompatActivity implements OnMapReadyCallback,
        View.OnClickListener,
        BookingTypeAdapter.BookingTypeAdapterRecyclerViewItemClickListener,
        RentalDataAdapter.RentalRecyclerViewItemClickListener {
    private FusedLocationProviderClient mFusedLocationProviderClient = null;

    private SettingsClient settingsClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private SearchIntent searchIntent = null;
    private SearchService searchService = null;
    private HuaweiMap huaweiMap = null;
    private Marker movingCabMarker = null;
    private Polyline grayPolyline = null;
    private Polyline blackPolyline = null;
    private LatLng previousLatLng = null;
    private LatLng carCurrentLatLng = null;
    private LatLng driverCurrentLatLng = null;
    private LatLng userCurrentLatLng = null;
    private ArrayList<LatLng> mDriverUserPathsList = new ArrayList();
    private ArrayList<CabTypeModal> cabTypeArrayList = null;
    private ArrayList<RentalTypeModal> rentalTypeArrayList = new ArrayList();
    private ArrayList<NearbyCabsModel> nearByCabsArrayList = new ArrayList();
    private BookingTypeModal bookingTypeModel = null;
    private RentalDialog rentalDialog = null;
    private final Handler mUIHandler = new Handler();
    private Handler mHandlerCarMovement = null;
    private Runnable mRunnableCarMovement = null;
    private MapView mapView;
    private TextView pickUpLocation, dropLocation, tvOtp, tvModel, tvCabNumber, tvBookingType, tvName;
    private Button confirmBooking;
    private ImageView customCurrentLocation, customPickDestination, imgDriver;
    private RatingBar ratingBar;
    private ViewGroup layoutBookingDetails, layoutBooking;
    private ProgressBar progressBar;
    private RecyclerView bookingRecylerView, cabTypeRecylerView;
    private Boolean isSourceAddressField = false;
    private Boolean isDestinationReached = false;
    private Double pickupLat = 0.0;
    private Double pickupLng = 0.0;
    private Double dropLat = 0.0;
    private Double dropLng = 0.0;
    private int index = 0;
    private static final int PERMISSIONS_REQUEST_CODE_ONE = 1;
    private static final int PERMISSIONS_REQUEST_CODE_TWO = 2;
    private static final int SEARCH_SOURCE_ADDRESS_REQUEST_CODE = 100;
    private static final int SEARCH_DESTINATION_ADDRESS_REQUEST_CODE = 101;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final boolean RETURNPOI_VALUE = true;
    private static final int HANDLER_ONE_SECOND = 1000;
    private static final int HANDLER_TWO_FIVE_SECOND = 2500;
    private static final int HANDLER_FIVE_SECOND = 5000;
    private static final String QUERY_VALUE = "India";
    private static final int RADIUS = 10000;
    private static final String LANGUAGE_VALUE = "en";
    private static final int PAGEINDEX = 1;
    private static final int PAGESIZE = 5;
    private static final String POLITICALVIEW_VALUE = "IN";
    private static final String RETURN_CODE = "returnCode";
    private static final String SITES = "sites";
    private static final String FORMAT_ADDRESS = "formatAddress";
    private static final String LNG = "lng";
    private static final String LAT = "lat";
    private static final String LOCATION = "location";
    private static final String LANGUAGE = "language";
    private static final String POLITICALVIEW = "politicalView";
    private static final String ORIGIN = "origin";
    private static final String DESTINATION = "destination";
    private static final String ROUTES = "routes";
    private static final String PATHS = "paths";
    private static final String STEPS = "steps";
    private static final String POLYLINE = "polyline";
    private static final String ERRORMSG = "errorMsg";
    private static final String RETURNPOI = "returnPoi";
    private static final String BOOKINGTYPE = "BookingType";
    private static final String BOOKINGTYPEID = "BookingType_Id";
    private static final String CABTYPE = "Cabtype";
    private static final String CAB_ID = "cab_id";
    private static final String CAB_TYPE = "cab_type";
    private static final String CAB_SEATS = "cab_seats";
    private static final String BOOKING_TYPE = "booking_type";
    private static final String DRIVER_ID = "DriverId";
    private static final String DRIVER_NAME = "DriverName";
    private static final String CAR_NUMBER = "Car_Number";
    private static final String PHONE_NUMBER = "Phone_Number";
    private static final String RATING = "Rating";
    private static final String TOTAL_NO_OF_TRAVEL = "Total_no_of_travel";
    private static final String DRIVER_JSON_PATH = "DriverDetail.json";
    private static final String BOOKINGCABTYPE_JSON_PATH = "BookingCabType.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cabbooking);

        mapView = findViewById(R.id.mapView);
        pickUpLocation = findViewById(R.id.pickUpLocation);
        dropLocation = findViewById(R.id.dropLocation);
        tvOtp = findViewById(R.id.tvOtp);
        tvModel = findViewById(R.id.tvModel);
        tvCabNumber = findViewById(R.id.tvCabNumber);
        tvBookingType = findViewById(R.id.tvBookingType);
        tvName = findViewById(R.id.tvName);
        confirmBooking = findViewById(R.id.confirmBooking);
        customCurrentLocation = findViewById(R.id.customCurrentLocation);
        customPickDestination = findViewById(R.id.customPickDestination);
        imgDriver = findViewById(R.id.img_driver);
        layoutBookingDetails = findViewById(R.id.layout_bookingdetails);
        layoutBooking = findViewById(R.id.layout_booking);
        ratingBar = findViewById(R.id.ratingBar);
        progressBar = findViewById(R.id.progress_bar);
        bookingRecylerView = findViewById(R.id.bookingRecylerView);
        cabTypeRecylerView = findViewById(R.id.cabTypeRecylerView);

        // Initialize the location update callback
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    // Process the location callback result.
                    Location location = locationResult.getLocations().get(0);
                    // Define logic for processing the Location object upon success.
                    moveCamera(new LatLng(location.getLatitude(), location.getLongitude()));
                    pickupLat = location.getLatitude();
                    pickupLng = location.getLongitude();
                    userCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    // Calling Nearby Search Site API
                    getNearByCabsSite();
                    isSourceAddressField = true;
                    // Calling Reverse Geocode Site API
                    reverseGeocoding(pickupLat, pickupLng);
                    // Stop requestlocationupdate callback, once retrieved current location
                    removeLocatonUpdates();
                }
            }
        };

        // Location service
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Obtain a SettingsClient instance
        settingsClient = LocationServices.getSettingsClient(this);
        // Checking permission
        if (checkPermission()) {
            checkLocationSettings();
        } else {
            requestPermission();
        }

        // Add map instance
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        // getMapAsync() to register the callback
        mapView.getMapAsync(this);

        // Initialize Search Service
        getLocationFromService();

        // Initliaze Onclicklistener
        pickUpLocation.setOnClickListener(this);
        dropLocation.setOnClickListener(this);
        confirmBooking.setOnClickListener(this);
        customCurrentLocation.setOnClickListener(this);

        // Fetching booking cab type arraylist from assets file
        parseBookingCabTypeJsonFromAssets();
    }

    /**
     * Checks the permissions
     * @return boolean value
     */
    public boolean checkPermission() {
        int fineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        return fineLocation == PackageManager.PERMISSION_GRANTED &&
                coarseLocation == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Add runtime permissions for creating a location provider client in the activity.
     */
    private void requestPermission() {
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
                String[] strings = new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                };
                ActivityCompat.requestPermissions(this, strings, PERMISSIONS_REQUEST_CODE_ONE);
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
                String[] strings = new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        ACCESS_BACKGROUND_LOCATION
                };
                ActivityCompat.requestPermissions(this, strings, PERMISSIONS_REQUEST_CODE_TWO);
            }
        }
    }

    /**
     * Use the onRequestPermissionsResult function to receive the permission verification result.
     * @param requestCode  as int
     * @param permissions  as String[]
     * @param grantResults as int[]
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Check whether requestCode is set to the value of PERMISSIONS_REQUEST_CODE_ONE, PERMISSIONS_REQUEST_CODE_TWO during permission application, and then check whether the permission is enabled.
        if (requestCode == PERMISSIONS_REQUEST_CODE_ONE || requestCode == PERMISSIONS_REQUEST_CODE_TWO) {
            if (grantResults.length > 0) {
                boolean fineLocation = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean coarseLocation = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (fineLocation
                        && coarseLocation) {
                    checkLocationSettings();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.access_needed), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * Creating location request.
     * @return LocationRequest
     */
    private LocationRequest createLocationRequest() {
        mLocationRequest = new LocationRequest();
        //Set the location update interval (int milliseconds).
        mLocationRequest.setInterval(HANDLER_ONE_SECOND);
        //Set the weight.
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    /**
     * Checking the device location settings.
     */
    private void checkLocationSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(createLocationRequest());
        LocationSettingsRequest locationSettingsRequest = builder.build();
        // Before requesting location update, invoke checkLocationSettings to check device settings.
        settingsClient.checkLocationSettings(locationSettingsRequest)
                // Define callback for success in checking the device location settings.
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        // Initiate location requests when the location settings meet the requirements.
                        mFusedLocationProviderClient
                                .requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())
                                // Define callback for success in requesting location updates.
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }
                                })
                                // Define callback for failure in requesting location updates.
                                .addOnCanceledListener(new OnCanceledListener() {
                                    @Override
                                    public void onCanceled() {
                                        getLastLocation();
                                    }
                                });
                    }
                })
                // Define callback for failure in checking the device location settings.
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // Device location settings do not meet the requirements.
                    }
                });
    }

    /**
     * When requesting location updates is stopped, the mLocationCallback object must be the same as LocationCallback in the requestLocationUpdates method.
     */
    private void removeLocatonUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
                // Define callback for success in stopping requesting location updates.
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                // Define callback for failure in stopping requesting location updates.
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                    }
                });
    }

    /**
     * Declare the SearchService object and use the SearchServiceFactory class to instantiate the object.
     * To create a SearchService instance, you need to pass the Context and API key parameters.
     * The API key is generated together with the agconnect-services.json file when you create an app in AppGallery Connect.
     * Note: the API key must be encoded using encodeURI.
     */
    private void getLocationFromService() {
        searchService =
                SearchServiceFactory.create(
                        this,
                        APIENCODEDKEY
                );
    }

    /**
     * Add the life cycle method of the MapView.
     */
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    /**
     * Call the onMapReady callback to obtain the HuaweiMap object.
     * @param map as HuaweiMap
     * Fetching the current location will be pointed on the huawei map.
     * Listener is set for camera moving events, the application layer can listen on the camera moving status.
     * When the camera starts to move, the onCameraMoveStarted() method of HuaweiMap.OnCameraMoveStartedListener will be called.
     */
    @Override
    public void onMapReady(HuaweiMap map) {
        huaweiMap = map;
        huaweiMap.setMyLocationEnabled(true);
        huaweiMap.getUiSettings().setMyLocationButtonEnabled(false);

        huaweiMap.setOnCameraMoveStartedListener(i -> {
            switch (i) {
                case HuaweiMap.OnCameraMoveStartedListener.REASON_GESTURE:
                    if (customPickDestination.getVisibility() == VISIBLE) {
                        LatLng midLatLng = huaweiMap.getCameraPosition().target;
                        if (midLatLng != null) {
                            dropLat = midLatLng.latitude;
                            dropLng = midLatLng.longitude;
                            isSourceAddressField = false;
                            // Reverse Geocode Site API
                            reverseGeocoding(dropLat, dropLng);
                        }
                    }
                    break;
                case HuaweiMap.OnCameraMoveStartedListener.REASON_API_ANIMATION:
                    //The user tapped something on the map.
                    break;
                case HuaweiMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION:
                    //The app moved the camera.
                    break;
            }
        });
    }

    /**
     * Fetching the current location will be pointed on the huawei map with the help of lastlocation on FusedLocationProviderClient.
     */
    private void getLastLocation() {
        // Obtain the last known location.
        mFusedLocationProviderClient.getLastLocation()
                // Define callback for success in obtaining the last known location.
                .addOnSuccessListener(location -> {
                    if (location == null) {
                        return;
                    }
                    // Define logic for processing the Location object upon success.
                    moveCamera(new LatLng(location.getLatitude(), location.getLongitude()));
                    pickupLat = location.getLatitude();
                    pickupLng = location.getLongitude();
                    userCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    // Calling Nearby Search Site API
                    getNearByCabsSite();
                    isSourceAddressField = true;
                    // Calling Reverse Geocode Site API
                    reverseGeocoding(pickupLat, pickupLng);
                })
                // Define callback for failure in obtaining the last known location.
                .addOnFailureListener(e -> {
                });
    }

    /**
     * Create a NearbySearchRequest object, which is used as the request body for nearby place search. In the request, the location parameter is mandatory and other parameters are optional.
     * Create a SearchResultListener object to listen for the search result.
     * Obtain the NearbySearchResponse object using the created SearchResultListener object. You can obtain a Site object from the NearbySearchResponse object and then parse it to obtain specific search results.
     */
    private void getNearByCabsSite() {
        // Instantiate the SearchService object.
        searchService = SearchServiceFactory.create(this, APIENCODEDKEY);
        // Create a request body.
        NearbySearchRequest request = new NearbySearchRequest();
        if (userCurrentLatLng == null) {
            return;
        }
        Coordinate coordinate = new Coordinate(userCurrentLatLng.latitude, userCurrentLatLng.longitude);
        request.setLocation(coordinate);
        request.setQuery(QUERY_VALUE);
        request.setRadius(RADIUS);
        request.setPoiType(LocationType.ADDRESS);
        request.setLanguage(LANGUAGE_VALUE);
        request.setPageIndex(PAGEINDEX);
        request.setPageSize(PAGESIZE);
        request.setPoliticalView(POLITICALVIEW_VALUE);
        // Create a search result listener.
        SearchResultListener<NearbySearchResponse> resultListener = new SearchResultListener<NearbySearchResponse>() {
            // Return search results upon a successful search.
            @Override
            public void onSearchResult(NearbySearchResponse results) {
                if (results == null || results.getTotalCount() <= 0) {
                    return;
                }
                List<Site> sites = results.getSites();
                if (sites == null || sites.size() == 0) {
                    return;
                }
                for (Site site : sites) {
                    if (site == null) {
                        return;
                    }
                    NearbyCabsModel nearByCabsModel = new NearbyCabsModel(site.siteId, Double.toString(site.location.lat), Double.toString(site.location.lng));
                    nearByCabsArrayList.add(nearByCabsModel);
                }
                addNearByCabsMarker(nearByCabsArrayList);
            }
            // Return the result code and description upon a search exception.
            @Override
            public void onSearchError(SearchStatus status) {
            }
        };
        // Call the nearby place search API.
        searchService.nearbySearch(request, resultListener);
    }

    /**
     * Parse the response body to obtain specific search results and pointed on the huawei map using marker.
     * @param responseBody as ArrayList<NearbyCabsModel>
     */
    private void addNearByCabsMarker(ArrayList<NearbyCabsModel> responseBody) {
        nearByCabsArrayList = responseBody;
        for (NearbyCabsModel item : responseBody) {
            if (item == null) {
                return;
            }
            LatLng latlng = new LatLng(Double.parseDouble(item.getLat()), Double.parseDouble(item.getLng()));
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_white_car);
            huaweiMap.addMarker(
                    new MarkerOptions().position(latlng).anchor(0.5f, 0.9f).icon(icon)
                            .snippet(latlng.toString())
            );
        }
    }

    /**
     * Convert current coordinate into address with the help of reverseGeocode API.
     * @param lat latitude as Double value
     * @param lng longitude as Double value
     * Obtain the response from the reverseGeocode API. Parse it and get address value from json response with the help of formatAddress key and Render result into input field
     */
    private void reverseGeocoding(Double lat, Double lng) {
        JSONObject jsonRequest = new JSONObject();
        JSONObject location = new JSONObject();
        try {
            location.put(LNG, lng);
            location.put(LAT, lat);
            jsonRequest.put(LOCATION, location);
            jsonRequest.put(LANGUAGE, LANGUAGE_VALUE);
            jsonRequest.put(POLITICALVIEW, POLITICALVIEW_VALUE);
            jsonRequest.put(RETURNPOI, RETURNPOI_VALUE);
        } catch (JSONException e) {
        }
        RequestBody requestBody = RequestBody.create(JSON, jsonRequest.toString());
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(SITEBASEURL + REVERSEGEOCODESUFFIXURL + APIENCODEDKEY)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                try {
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    String responseCode = jsonResponse.getString(RETURN_CODE);
                    if (responseCode.contentEquals("0")) {
                        JSONArray jsonArraySites = jsonResponse.getJSONArray(SITES);
                        JSONObject jsonObjectDetail = jsonArraySites.getJSONObject(0);
                        String address = jsonObjectDetail.getString(FORMAT_ADDRESS);
                        if (address != null) {
                            if (isSourceAddressField) {
                                pickUpLocation.setText(address);
                            } else {
                                dropLocation.setText(address);
                            }
                        }
                        runOnUiThread(() -> {
                            // Stuff that updates the UI
                            if (pickupLat > 0 && dropLat > 0) {
                                confirmBooking.setEnabled(true);
                            }
                        });
                    }
                } catch (JSONException e) {
                }
            }
        });
    }

    /**
     * Implementing onclick event
     * @param p0 as View
     */
    @Override
    public void onClick(View p0) {
        int id = p0.getId();
        if (id == R.id.pickUpLocation) {
            locationBox(SEARCH_SOURCE_ADDRESS_REQUEST_CODE);
        } else if (id == R.id.dropLocation) {
            locationBox(SEARCH_DESTINATION_ADDRESS_REQUEST_CODE);
        } else if (id == R.id.confirmBooking) {
            if (!TextUtils.isEmpty(pickUpLocation.getText()) && !TextUtils.isEmpty(dropLocation.getText())) {
                showConfirmDialog();
            } else {
                ViewUtils.toast(this, getString(R.string.error_message_source_destination_is_needed));
            }
        } else if (id == R.id.customCurrentLocation) {
            getLastLocation();
        }
    }

    /**
     * Declare search intent. Its help to navigating screen to place search suggestion site sdk. By the help of android startActivityForResult() method, we can get result from another activity.
     * @param requestcode as Integer
     */
    private void locationBox(int requestcode) {
        searchIntent = new SearchIntent();
        searchIntent.setApiKey(APIENCODEDKEY);
        Intent intent = searchIntent.getIntent(this);
        startActivityForResult(intent, requestcode);
    }

    /**
     * Callback method  to get the message form other Activity
     * @param requestCode as Integer
     * @param resultCode as Integer
     * @param data as Intent
     */
    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            @Nullable Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEARCH_SOURCE_ADDRESS_REQUEST_CODE:
                if (SearchIntent.isSuccess(resultCode)) {
                    isSourceAddressField = true;
                    Site site = searchIntent.getSiteFromIntent(data);
                    if (site != null) {
                        pickUpLocation.setText(site.name);
                        querySuggestion();
                    }
                }
                break;
            case SEARCH_DESTINATION_ADDRESS_REQUEST_CODE:
                if (SearchIntent.isSuccess(resultCode)) {
                    isSourceAddressField = false;
                    Site site = searchIntent.getSiteFromIntent(data);
                    if (site != null) {
                        dropLocation.setText(site.name);
                        querySuggestion();
                    }
                }
                break;
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
    private void querySuggestion() {
        QuerySuggestionRequest request = new QuerySuggestionRequest();
        String query;
        if (isSourceAddressField) {
            query = pickUpLocation.getText().toString();
        } else {
            query = dropLocation.getText().toString();
        }
        if (!TextUtils.isEmpty(query)) {
            request.query = query;
        }
        searchService.querySuggestion(
                request,
                searchResultListener
        );
    }

    /**
     * Create a SearchResultListener object to listen for the search result.
     * Obtain the search result from onSearchResult, and display places in the search result.
     */
    private final SearchResultListener<QuerySuggestionResponse> searchResultListener = new SearchResultListener<QuerySuggestionResponse>() {
        // Return search results upon a successful search.
        @Override
        public void onSearchResult(QuerySuggestionResponse results) {
            if (results == null) {
                return;
            }
            List<Site> sites = results.getSites();
            if (sites == null || sites.size() <= 0) {
                return;
            }
            for (Site site : sites) {
                if (site == null) {
                    return;
                }
                Coordinate location = site.location;
                if (location == null) {
                    return;
                }
                if (isSourceAddressField) {
                    pickupLat = location.lat;
                    pickupLng = location.lng;
                    if (dropLat > 0) {
                        driverCurrentLatLng = new LatLng(dropLat, dropLng);
                        userCurrentLatLng = new LatLng(pickupLat, pickupLng);
                        confirmBooking.setEnabled(true);
                        moveCamera(userCurrentLatLng);
                    }
                } else {
                    dropLat = location.lat;
                    dropLng = location.lng;
                    if (pickupLat > 0) {
                        driverCurrentLatLng = new LatLng(dropLat, dropLng);
                        userCurrentLatLng = new LatLng(pickupLat, pickupLng);
                        confirmBooking.setEnabled(true);
                        moveCamera(driverCurrentLatLng);
                    }
                }
            }
        }
        // Return the result code and description upon a search exception.
        @Override
        public void onSearchError(SearchStatus status) {
        }
    };

    /**
     * Alert dialog can be used to interrupt and ask the user choice to continue or discontinue.
     * Click NO to Ignore and CONFIRM to confirm the booking.
     * parseDriverDetailJsonFromAssets() method is used to fetching driver details from the asset json file.
     */
    private void showConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_booking_title))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                    huaweiMap.getUiSettings().setScrollGesturesEnabled(false);
                    ViewUtils.show(progressBar);

                    tvOtp.setText(getString(R.string.otp));

                    // Fetching driver details from the asset json file
                    parseDriverDetailJsonFromAssets();

                    mUIHandler.postDelayed(() -> {
                        ViewUtils.hide(progressBar);
                        huaweiMap.getUiSettings().setScrollGesturesEnabled(true);
                        confirmCabBooking();
                    }, HANDLER_ONE_SECOND);
                }).setNegativeButton(getString(R.string.no), (dialog, which) -> {
            huaweiMap.getUiSettings().setScrollGesturesEnabled(true);
            dialog.dismiss();
        });
        // Creating dialog box
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Handle the UI once confirm booking.
     * Calling direction API for draw the route between source and destination.
     */
    private void confirmCabBooking() {
        layoutBookingDetails.setVisibility(VISIBLE);
        layoutBooking.setVisibility(GONE);
        customPickDestination.setVisibility(GONE);
        pickUpLocation.setEnabled(false);
        dropLocation.setEnabled(false);
        if (nearByCabsArrayList.size() > 0) {
            double lat = Double.parseDouble(nearByCabsArrayList.get(0).getLat());
            double lng = Double.parseDouble(nearByCabsArrayList.get(0).getLng());
            driverCurrentLatLng = new LatLng(lat, lng);
        } else {
            ViewUtils.toast(this, getString(R.string.error_message_cab_not_available_your_location));
            return;
        }
        userCurrentLatLng = new LatLng(pickupLat, pickupLng);
        directionAPI(driverCurrentLatLng, userCurrentLatLng);
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
    private void directionAPI(LatLng startLoc, LatLng endLoc) {
        huaweiMap.clear();
        JSONObject jsonRequest = new JSONObject();
        JSONObject origin = new JSONObject();
        JSONObject destination = new JSONObject();
        if (startLoc == null || endLoc == null) {
            return;
        }
        try {
            origin.put(LAT, startLoc.latitude);
            origin.put(LNG, startLoc.longitude);
            destination.put(LAT, endLoc.latitude);
            destination.put(LNG, endLoc.longitude);
            jsonRequest.put(ORIGIN, origin);
            jsonRequest.put(DESTINATION, destination);
        } catch (JSONException e) {
        }
        RequestBody requestBody = RequestBody.create(JSON, jsonRequest.toString());
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(DRIVINGBASEURL + DRIVINGSUFFIXURL + APIENCODEDKEY)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (responseBody != null) {
                    generateRouteForMap(responseBody);
                }
            }
        });
    }

    /**
     * Obtain the response from the direction API. Parse it and draw route using polyline.
     * @param routes contains responsebody from the direction API as string
     */
    private void generateRouteForMap(String routes) {
        mDriverUserPathsList = new ArrayList();
        try {
            JSONObject jsonResponse = new JSONObject(routes);
            JSONArray routesList = jsonResponse.getJSONArray(ROUTES);
            if (routesList.length() == 0) {
                return;
            }
            JSONObject route = routesList.getJSONObject(0);
            if (route == null) {
                return;
            }
            JSONArray paths = route.getJSONArray(PATHS);
            if (paths.length() == 0) {
                return;
            }
            for (int i = 0; i < paths.length(); i++) {
                JSONObject path = paths.getJSONObject(i);
                JSONArray steps = path.getJSONArray(STEPS);
                for (int j = 0; j < steps.length(); j++) {
                    JSONObject step = steps.getJSONObject(j);
                    JSONArray polyline = step.getJSONArray(POLYLINE);
                    for (int k = 0; k < polyline.length(); k++) {
                        if (j > 0 && k == 0) {
                            continue;
                        }
                        JSONObject line = polyline.getJSONObject(k);
                        if (line != null) {
                            double lat = line.getDouble(LAT);
                            double lng = line.getDouble(LNG);
                            LatLng latLng = new LatLng(lat, lng);
                            mDriverUserPathsList.add(latLng);
                        }
                    }
                }
            }
            mHandler.sendEmptyMessage(0);
        } catch (JSONException e) {
        }
    }

    /**
     * Rendering the result on Huawei Map using Handler.
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    driverComingToYourLocation();
                    break;
                case 1:
                    Bundle bundle = msg.getData();
                    String errorMsg = bundle.getString(ERRORMSG);
                    ViewUtils.toast(HuaweiMapActivity.this, errorMsg);
                    break;
            }
        }
    };

    /**
     * Showing path from driver location to customer location using polyline and move cab marker as well
     */
    private void driverComingToYourLocation() {
        if (isDestinationReached) {
            showDefaultLocationOnMap(userCurrentLatLng);
        } else {
            ViewUtils.sendNotification(
                    HuaweiMapActivity.this,
                    getString(R.string.notification_message_body) + tvOtp.getText().toString(),
                    getString(R.string.notification_message_ride_on_the_way)
            );
            showDefaultLocationOnMap(driverCurrentLatLng);
        }

        new Handler().postDelayed(() -> {
            showPath(mDriverUserPathsList);
            showMovingCab(mDriverUserPathsList);
        }, HANDLER_THREE_SECOND);
    }

    /**
     * Call move and animate camera method
     * @param latLng as LatLng
     */
    private void showDefaultLocationOnMap(LatLng latLng) {
        if (latLng == null) {
            return;
        }
        moveCamera(latLng);
        animateCamera(latLng);
    }

    /**
     * Move the map camera in non-animation mode.
     * @param latLng as LatLng
     */
    private void moveCamera(LatLng latLng) {
        if (latLng == null) {
            return;
        }
        huaweiMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
    }

    /**
     * Move the map camera in animation mode.
     * @param latLng as LatLng
     */
    private void animateCamera(LatLng latLng) {
        if (latLng == null) {
            return;
        }
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17f).build();
        huaweiMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /**
     * Rendering the route on huawei map using polyline
     * @param latLngList as ArrayList<LatLng>
     */
    private void showPath(ArrayList<LatLng> latLngList) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (latLngList == null) {
            return;
        }
        for (LatLng latLng : latLngList) {
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();
        huaweiMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 2));

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.GRAY);
        polylineOptions.width(5f);
        polylineOptions.addAll(latLngList);
        grayPolyline = huaweiMap.addPolyline(polylineOptions);

        PolylineOptions blackPolylineOptions = new PolylineOptions();
        blackPolylineOptions.color(Color.BLACK);
        blackPolylineOptions.width(5f);
        blackPolyline = huaweiMap.addPolyline(blackPolylineOptions);

        Marker originMarker = addOriginDestinationMarker(latLngList.get(0));
        originMarker.setAnchor(0.5f, 0.5f);
        Marker destinationMarker = addOriginDestinationMarker(latLngList.get(latLngList.size() - 1));
        destinationMarker.setAnchor(0.5f, 0.5f);

        ValueAnimator polylineAnimator = AnimationUtils.polylineAnimator();
        polylineAnimator.addUpdateListener(valueAnimator -> {
            int percentValue = (int) (valueAnimator.getAnimatedValue());
            int index = (grayPolyline.getPoints().size()) * (int) (percentValue / 100.0f);
            blackPolyline.setPoints(grayPolyline.getPoints().subList(0, index));
        });
        polylineAnimator.start();
    }

    /**
     * Pointing marker on huawei map using source and destination coordinate with the help of marker
     * @param latLng as Marker
     * @return Marker
     */
    private Marker addOriginDestinationMarker(LatLng latLng) {
        BitmapDescriptor bitmapDescriptor =
                BitmapDescriptorFactory.fromBitmap(MapUtils.getOriginDestinationMarkerBitmap());
        return huaweiMap.addMarker(
                new MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
        );
    }

    /**
     * Automatically move the cab from source to destination with response array and render it on huawei map using handler with duration.
     * @param cabLatLngList as ArrayList<LatLng>
     */
    private void showMovingCab(ArrayList<LatLng> cabLatLngList) {
        index = 0;
        mHandlerCarMovement = new Handler();
        mRunnableCarMovement = () -> {
            if (index < mDriverUserPathsList.size() - 1) {
                updateCarLocation(cabLatLngList.get(index));
                mHandlerCarMovement.postDelayed(mRunnableCarMovement, HANDLER_THREE_SECOND);
                ++index;
            } else {
                mHandlerCarMovement.removeCallbacks(mRunnableCarMovement);
                if (!isDestinationReached) {
                    ViewUtils.toast(HuaweiMapActivity.this, getString(R.string.your_ride_is_here));
                    ViewUtils.sendNotification(
                            HuaweiMapActivity.this,
                            getString(R.string.notification_message_body) + tvOtp.getText().toString(),
                            getString(R.string.your_ride_is_here)
                    );
                    travelWithCustomer();
                } else {
                    ViewUtils.toast(HuaweiMapActivity.this, getString(R.string.thanks_message));
                    ViewUtils.sendNotification(
                            HuaweiMapActivity.this,
                            getString(R.string.destination_reached_message),
                            getString(R.string.thanks_title)
                    );
                    ViewUtils.show(progressBar);
                    mUIHandler.postDelayed(() -> {
                        ViewUtils.hide(progressBar);
                        clearMapView();
                    }, HANDLER_TWO_FIVE_SECOND);
                }
            }
        };
        mHandlerCarMovement.postDelayed(mRunnableCarMovement, HANDLER_FIVE_SECOND);
    }

    /**
     * Calling again direction api when driver reached customer place for pickup.
     */
    private void travelWithCustomer() {
        isDestinationReached = true;
        // Waiting for customer
        ViewUtils.show(progressBar);
        mUIHandler.postDelayed(() -> {
            ViewUtils.hide(progressBar);
            movingCabMarker = null;
            previousLatLng = null;
            driverCurrentLatLng = new LatLng(dropLat, dropLng);
            userCurrentLatLng = new LatLng(pickupLat, pickupLng);
            if (driverCurrentLatLng != null && userCurrentLatLng != null) {
                directionAPI(userCurrentLatLng, driverCurrentLatLng);
            }
        }, HANDLER_FIVE_SECOND);
    }

    /**
     * When cab is on moving, update the cab marker on huawei map with animation using current location.
     * @param latLng as LatLng
     */
    private void updateCarLocation(LatLng latLng) {
        if (latLng == null) {
            return;
        }
        if (movingCabMarker == null) {
            movingCabMarker = addCarMarker(latLng);
        }
        if (previousLatLng == null) {
            carCurrentLatLng = latLng;
            previousLatLng = carCurrentLatLng;
            movingCabMarker.setPosition(carCurrentLatLng);
            movingCabMarker.setAnchor(0.5f, 0.5f);
            animateCamera(carCurrentLatLng);
        } else {
            previousLatLng = carCurrentLatLng;
            carCurrentLatLng = latLng;
            ValueAnimator valueAnimator = AnimationUtils.carAnimator();
            valueAnimator.addUpdateListener(va -> {
                if (carCurrentLatLng != null && previousLatLng != null) {
                    float multiplier = va.getAnimatedFraction();
                    LatLng nextLocation = new LatLng(
                            multiplier * carCurrentLatLng.latitude + (1 - multiplier) * previousLatLng.latitude,
                            multiplier * carCurrentLatLng.longitude + (1 - multiplier) * previousLatLng.longitude
                    );
                    movingCabMarker.setPosition(nextLocation);
                    Float rotation = MapUtils.getRotation(previousLatLng, nextLocation);
                    if (!rotation.isNaN()) {
                        movingCabMarker.setRotation(rotation);
                    }
                    movingCabMarker.setAnchor(0.5f, 0.5f);
                    animateCamera(nextLocation);
                }
            });
            valueAnimator.start();
        }
    }

    /**
     * Pointing cab marker on huawei map using latlng
     * @param latLng as LatLng
     * @return Marker
     */
    private Marker addCarMarker(LatLng latLng) {
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(
                MapUtils.getCabBitmap(
                        this
                )
        );
        return huaweiMap.addMarker(
                new MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
        );
    }

    @Override
    public void clickOnItem(BookingTypeModal bookingType) {
        bookingTypeModel = bookingType;
        if (bookingTypeModel.getBookingTypeName().contentEquals(RENTAL)) {
            openRentalDialog();
        }
        cabTypeArrayList = bookingTypeModel.getCabTypeModalList();
        updateCabTypeAdapter(cabTypeArrayList);
    }

    /**
     * Showing rental dialog when user select rental booking type
     */
    private void openRentalDialog() {
        rentalTypeArrayList = new ArrayList();
        RentalTypeModal rent1 = new RentalTypeModal(getString(R.string.one_hour_ten_kms));
        RentalTypeModal rent2 = new RentalTypeModal(getString(R.string.two_hour_twenry_kms));
        RentalTypeModal rent3 = new RentalTypeModal(getString(R.string.four_hour_fouty_kms));
        RentalTypeModal rent4 = new RentalTypeModal(getString(R.string.six_hour_sixty_kms));
        RentalTypeModal rent5 = new RentalTypeModal(getString(R.string.eight_hours_eighty_kms));
        RentalTypeModal rent6 = new RentalTypeModal(getString(R.string.ten_hours_hundred_kms));
        rentalTypeArrayList.add(rent1);
        rentalTypeArrayList.add(rent2);
        rentalTypeArrayList.add(rent3);
        rentalTypeArrayList.add(rent4);
        rentalTypeArrayList.add(rent5);
        rentalTypeArrayList.add(rent6);
        RentalDataAdapter dataAdapter = new RentalDataAdapter(rentalTypeArrayList, this);
        rentalDialog = new RentalDialog(this, dataAdapter);
        // if we know that the particular variable not null any time ,we can assign !! (not null operator ), then  it won't check for null, if it becomes null, it willthrow exception
        rentalDialog.show();
        rentalDialog.setCanceledOnTouchOutside(false);
    }

    /**
     * Callback method for close the rental dialog once user selected any item on list which shown on dialog.
     * @param data as String
     */
    @Override
    public void clickOnRentalItem(String data) {
        rentalDialog.dismiss();
    }

    /**
     * Fetching booking cab type arraylist from assets json file name "BookingCabType.json".
     * Parse it and construct array as bookingCabTypeResponse.
     */
    private void parseBookingCabTypeJsonFromAssets() {
        ArrayList<BookingTypeModal> bookingCabTypeResponse = new ArrayList<>();
        try {
            // Extension Function call here
            JSONArray bookingCabTypeArray = new JSONArray(ViewUtils.loadJSONFromAssets(HuaweiMapActivity.this, BOOKINGCABTYPE_JSON_PATH));
            for (int i = 0; i < bookingCabTypeArray.length(); i++) {
                BookingTypeModal bookingType = new BookingTypeModal();
                JSONObject bookingCabTypeObject = bookingCabTypeArray.getJSONObject(i);
                bookingType.setBookingTypeName(bookingCabTypeObject.getString(BOOKINGTYPE));
                bookingType.setBookingTypeId(bookingCabTypeObject.getString(BOOKINGTYPEID));
                JSONArray cabTypeJsonArray = bookingCabTypeObject.getJSONArray(CABTYPE);
                for (int j = 0; j < cabTypeJsonArray.length(); j++) {
                    CabTypeModal cabTypeModal = new CabTypeModal();
                    JSONObject cabTypeJsonObject = cabTypeJsonArray.getJSONObject(j);
                    cabTypeModal.setCabID(Integer.parseInt(cabTypeJsonObject.getString(CAB_ID)));
                    cabTypeModal.setCabTypeName(cabTypeJsonObject.getString(CAB_TYPE));
                    cabTypeModal.setCabSeats(cabTypeJsonObject.getString(CAB_SEATS));
                    cabTypeModal.setBookingTypeName(cabTypeJsonObject.getString(BOOKING_TYPE));
                    bookingType.getCabTypeModalList().add(cabTypeModal);
                }
                bookingCabTypeResponse.add(bookingType);
            }
        } catch (JSONException e) {
        }

        updateBookingTypeAdapter(bookingCabTypeResponse);
    }

    /**
     * Parse it and render array result on cab type recyclerview in HORIZONTAL View.
     * @param bookingTypeResponse as ArrayList<BookingTypeModal>
     */
    private void updateBookingTypeAdapter(ArrayList<BookingTypeModal> bookingTypeResponse) {
        bookingRecylerView.setLayoutManager(new LinearLayoutManager(
                this,
                HORIZONTAL,
                false
        ));
        bookingRecylerView.setAdapter(new BookingTypeAdapter(bookingTypeResponse, this));
        cabTypeArrayList = bookingTypeResponse.get(0).getCabTypeModalList();
        updateCabTypeAdapter(cabTypeArrayList);
    }

    /**
     * Updating cab type recyclerview using adapter.
     * @param cabType as ArrayList<CabTypeModal>
     */
    private void updateCabTypeAdapter(ArrayList<CabTypeModal> cabType) {
        cabTypeRecylerView.setLayoutManager(new LinearLayoutManager(
                this,
                HORIZONTAL,
                false
        ));
        cabTypeRecylerView.setAdapter(new CabTypeAdapter(cabType));
    }

    /**
     * Fetching driver details object from assets json file name "DriverDetail.json".
     */
    private void parseDriverDetailJsonFromAssets() {
        DriverDetailsModel driverDetailResponse = new DriverDetailsModel();
        try {
            // Extension Function call here
            JSONObject driverDetailJsonResponse = new JSONObject(ViewUtils.loadJSONFromAssets(HuaweiMapActivity.this, DRIVER_JSON_PATH));
            driverDetailResponse.setDriverId(driverDetailJsonResponse.getString(DRIVER_ID));
            driverDetailResponse.setDriverName(driverDetailJsonResponse.getString(DRIVER_NAME));
            driverDetailResponse.setCarNumber(driverDetailJsonResponse.getString(CAR_NUMBER));
            driverDetailResponse.setPhoneNumber(driverDetailJsonResponse.getString(PHONE_NUMBER));
            driverDetailResponse.setRating(driverDetailJsonResponse.getString(RATING));
            driverDetailResponse.setTotalNoOfTravel(driverDetailJsonResponse.getString(TOTAL_NO_OF_TRAVEL));
            onUpdateDriverDetails(driverDetailResponse);
        } catch (JSONException e) {
        }
    }

    /**
     * Parse it and rendering driver details on UI once user confirm the booking.
     * @param driverResponse as DriverDetailsModel
     */
    private void onUpdateDriverDetails(DriverDetailsModel driverResponse) {
        displayDriverAvatar();
        tvCabNumber.setText(driverResponse.getCarNumber());
        ratingBar.setRating(Float.parseFloat(driverResponse.getRating()));
        if (bookingTypeModel != null) {
            tvBookingType.setText(bookingTypeModel.getBookingTypeName());
        }
        tvModel.setText(getString(R.string.Car_Name));
        tvName.setText(driverResponse.getDriverName());
    }

    /**
     * Rendering driver photo on ImageView using Picasso library.
     */
    private void displayDriverAvatar() {
        Picasso.get()
                .load(R.drawable.ic_dummy_user)
                .into(imgDriver);
    }

    /**
     * Reseting the UI.
     */
    private void clearMapView() {
        huaweiMap.clear();
        movingCabMarker = null;
        previousLatLng = null;
        isDestinationReached = false;
        layoutBookingDetails.setVisibility(GONE);
        layoutBooking.setVisibility(VISIBLE);
        pickUpLocation.setEnabled(true);
        dropLocation.setEnabled(true);
        huaweiMap.getUiSettings().setScrollGesturesEnabled(true);
        customPickDestination.setVisibility(VISIBLE);
        parseBookingCabTypeJsonFromAssets();
        getLastLocation();
    }
}