package com.dalilu.commandCenter.ui.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import com.dalilu.commandCenter.R;
import com.dalilu.commandCenter.bottomsheets.WelcomeNoticeBottomSheet;
import com.dalilu.commandCenter.databinding.ActivityMainBinding;
import com.dalilu.commandCenter.utils.AppConstants;
import com.dalilu.commandCenter.utils.DisplayViewUI;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import jahirfiquitiva.libs.fabsmenu.FABsMenu;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    public static String userName, userPhotoUrl;
    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;
    /**
     * Provides access to the Location Settings API.
     */
    private SettingsClient mSettingsClient;
    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;
    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    private LocationSettingsRequest mLocationSettingsRequest;
    /**
     * Callback for Location events.
     */
    private LocationCallback mLocationCallback;
    /**
     * Represents a geographical location.
     */
    private Location mCurrentLocation;
    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    private Boolean mRequestingLocationUpdates;

    private static Object mContext;
    private ActivityMainBinding activityMainBinding;

    /**
     * Time when the location was updated represented as a String.
     */
    public static String mLastUpdateTime, knownName, state, country, phoneNumber, userId;
    public static double latitude, longitude;
    private Geocoder geocoder;
    private CollectionReference alertsCollectionReference;
    FABsMenu faBsMenu;

    public static Context getAppContext() {
        return (Context) mContext;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mContext = getApplicationContext();
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);

        DatabaseReference locationDbRef = FirebaseDatabase.getInstance().getReference().child("Locations");
        alertsCollectionReference = FirebaseFirestore.getInstance().collection("Alerts");
        CollectionReference locationCollectionDbRef = FirebaseFirestore.getInstance().collection("Locations");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        geocoder = new Geocoder(this, Locale.getDefault());


        createLocationCallback();
        createLocationRequest();
        requestPermissions();


        initViews();

    }

    private void initViews() {
        BottomNavigationView navView = activityMainBinding.navView;
        Menu menu = navView.getMenu();
        MenuItem menuItemHome = menu.findItem(R.id.navigation_home);


        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_maps)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


        navView.setOnNavigationItemReselectedListener(item -> {

        });


        BadgeDrawable badgeDrawableHome = navView.getOrCreateBadge(menuItemHome.getItemId());

      /*  activityMainBinding.logOut.setOnClickListener(view -> DisplayViewUI.displayAlertDialog(view.getContext(),
                getString(R.string.logOut), getString(R.string.xcvv),
                getString(R.string.logMeOut), getString(R.string.cancel),
                (dialogInterface, i) -> {
                    if (i == -1) {

                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(view.getContext(), SplashScreenActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    } else if (i == -2) {
                        dialogInterface.dismiss();
                    }


                }));
*/

        Intent getUserDetailsIntent = getIntent();
        if (getUserDetailsIntent != null) {
            userName = getUserDetailsIntent.getStringExtra(AppConstants.USER_NAME);
            userPhotoUrl = getUserDetailsIntent.getStringExtra(AppConstants.USER_PHOTO_URL);
            userId = getUserDetailsIntent.getStringExtra(AppConstants.UID);
            phoneNumber = getUserDetailsIntent.getStringExtra(AppConstants.PHONE_NUMBER);



        }


        alertsCollectionReference.get().addOnCompleteListener(task -> {

            if (task.getResult().size() > 0)
                badgeDrawableHome.setNumber(task.getResult().size());

        });


    }


    void myIntent(@NonNull Class ctx) {

        Intent intent = new Intent(this, ctx);
        intent.putExtra(AppConstants.PHONE_NUMBER, phoneNumber);
        intent.putExtra(AppConstants.USER_PHOTO_URL, userPhotoUrl);
        intent.putExtra(AppConstants.USER_NAME, userName);
        intent.putExtra(AppConstants.STATE, state);
        intent.putExtra(AppConstants.COUNTRY, country);
        intent.putExtra(AppConstants.KNOWN_LOCATION, knownName);
        intent.putExtra(AppConstants.LATITUDE, latitude);
        intent.putExtra(AppConstants.LONGITUDE, longitude);
        intent.putExtra(AppConstants.UID, userId);


        startActivity(intent);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.switch_list_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.reportMenu){
            myIntent(ReportActivity.class);
        }
        return true;
    }

    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(AppConstants.KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        AppConstants.KEY_REQUESTING_LOCATION_UPDATES);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(AppConstants.KEY_LOCATION)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(AppConstants.KEY_LOCATION);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(AppConstants.KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(AppConstants.KEY_LAST_UPDATED_TIME_STRING);
            }
            // updateUI();
        }
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(AppConstants.UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(AppConstants.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Creates a callback for receiving location events.
     */
    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();
                updateLocationUI();
            }
        };
    }

    private void updateLocationUI() {
        if (mCurrentLocation != null) {
            Log.i(TAG, String.format(Locale.ENGLISH, "%s: %f", "lat",
                    mCurrentLocation.getLatitude()));
            Log.i(TAG, String.format(Locale.ENGLISH, "%s: %f", "lng",
                    mCurrentLocation.getLongitude()));
            Log.i(TAG, String.format(Locale.ENGLISH, "%s: %s",
                    "last update", mLastUpdateTime));

            latitude = mCurrentLocation.getLatitude();
            longitude = mCurrentLocation.getLongitude();


            try {
                List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);

                if (addressList != null) {
                    String address = addressList.get(0).getAddressLine(0);
                    state = addressList.get(0).getAdminArea();
                    country = addressList.get(0).getCountryName();
                    knownName = addressList.get(0).getFeatureName();


                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Uses a {@link LocationSettingsRequest.Builder} to build
     * a {@link LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == AppConstants.REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mRequestingLocationUpdates) {
                    Log.i(TAG, "Permission granted, updates requested, starting location updates");
                    startLocationUpdates();
                }
            } else {

                requestPermissions();


            }
        }
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            DisplayViewUI.displayAlertDialog(MainActivity.this,
                    getString(R.string.grantPerm),
                    getString(R.string.permission_rationale), getString(R.string.ok), (dialogInterface, i) -> {
// Request permission
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                AppConstants.REQUEST_PERMISSIONS_REQUEST_CODE);
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    AppConstants.REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Requests location updates from the FusedLocationApi. Note: we don't call this unless location
     * runtime permission has been granted.
     */
    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, locationSettingsResponse -> {
                    Log.i(TAG, "All location settings are satisfied.");

                    //noinspection MissingPermission
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                            mLocationCallback, Looper.myLooper());

                    //updateUI();
                })
                .addOnFailureListener(this, e -> {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                    "location settings ");

                            try {
                                // Show the dialog by calling startResolutionForResult(), and check the
                                // result in onActivityResult().
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(MainActivity.this, AppConstants.REQUEST_CHECK_SETTINGS);

                            } catch (IntentSender.SendIntentException sie) {
                                Log.i(TAG, "PendingIntent unable to execute request.");
                            }

                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            String errorMessage = getString(R.string.errMsg) + getString(R.string.fixIn);
                            Log.e(TAG, errorMessage);
                            DisplayViewUI.displayToast(getAppContext(), errorMessage);
                            mRequestingLocationUpdates = false;
                    }

                    //updateUI();
                });
    }



    @Override
    protected void onResume() {
        super.onResume();
        if (checkPermissions()) {
            buildLocationSettingsRequest();
            startLocationUpdates();
        }

    }


}