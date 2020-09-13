package com.dalilu.commandCenter.ui.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalilu.commandCenter.MapsActivity;
import com.dalilu.commandCenter.R;
import com.dalilu.commandCenter.adapters.HomeRecyclerAdapter;
import com.dalilu.commandCenter.databinding.ActivityMainBinding;
import com.dalilu.commandCenter.model.AlertItems;
import com.dalilu.commandCenter.services.LocationUpdatesService;
import com.dalilu.commandCenter.services.Utils;
import com.dalilu.commandCenter.utils.AppConstants;
import com.dalilu.commandCenter.utils.DisplayViewUI;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String KEY = "key";
    private static final int INITIAL_LOAD = 15;
    /**
     * Time when the location was updated represented as a String.
     */
    public static String knownName, state, country, phoneNumber, userId;
    private final ArrayList<AlertItems> arrayList = new ArrayList<>();
    private final CollectionReference collectionReference = FirebaseFirestore
            .getInstance()
            .collection("Alerts");
    ListenerRegistration registration;
    private Bundle mBundleState;
    private RecyclerView recyclerView;
    private HomeRecyclerAdapter adapter;
    private GridLayoutManager layoutManager;
    private static final String TAG = "MainActivity";
    public static String userName, userPhotoUrl;

    private ActivityMainBinding activityMainBinding;
    private Parcelable mState;
    private CollectionReference alertsCollectionReference;
    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    public static String address;
    public static Geocoder geocoder;
    // The BroadcastReceiver used to listen from broadcasts from the service.
    private MyReceiver myReceiver;

    // A reference to the service used to get location updates.
    private LocationUpdatesService mService = null;

    // Tracks the bound state of the service.
    private boolean mBound = false;

    // Monitors the state of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        myReceiver = new MyReceiver();
        geocoder = new Geocoder(this, Locale.getDefault());

        // Check that the user hasn't revoked permissions by going to Settings.
        if (Utils.requestingLocationUpdates(this)) {
            if (!checkPermissions()) {
                requestPermissions();
            }
        }

        Intent getUserDetailsIntent = getIntent();
        if (getUserDetailsIntent != null) {
            userName = getUserDetailsIntent.getStringExtra(AppConstants.USER_NAME);
            userPhotoUrl = getUserDetailsIntent.getStringExtra(AppConstants.USER_PHOTO_URL);
            userId = getUserDetailsIntent.getStringExtra(AppConstants.UID);
            phoneNumber = getUserDetailsIntent.getStringExtra(AppConstants.PHONE_NUMBER);

        }

        alertsCollectionReference = FirebaseFirestore.getInstance().collection("Alerts");


        initViews();
        runOnUiThread(this::fetchData);
    }

    private void fetchData() {

        // Create a query against the collection.
        Query query = collectionReference
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .limit(INITIAL_LOAD);

        registration = query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            // arrayList.clear();
            assert queryDocumentSnapshots != null;
            for (QueryDocumentSnapshot ds : queryDocumentSnapshots) {

                AlertItems alertItems = ds.toObject(AlertItems.class);
                //get data from model
                String address = alertItems.getAddress();
                String userName = alertItems.getUserName();
                String phoneNumber = alertItems.getPhoneNumber();
                long timeStamp = alertItems.getTimeStamp();
                String userPhotoUrl = alertItems.getUserPhotoUrl();
                String url = alertItems.getUrl();
                boolean isSolved = alertItems.isSolved();

                String id = ds.getId();
                String dateReported = alertItems.getDateReported();
//group data by images
                if (ds.getData().containsKey("image")) {

                    arrayList.add(new AlertItems(AppConstants.IMAGE_TYPE,
                            userName, userPhotoUrl, url, timeStamp, address, id, dateReported, isSolved));

                }
                //group data by Videos
                else if (ds.getData().containsKey("video")) {

                    arrayList.add(new AlertItems(AppConstants.VIDEO_TYPE,
                            id,
                            userName,
                            url,
                            userPhotoUrl,
                            timeStamp,
                            address,
                            isSolved
                    ));
                }

            }

            adapter.notifyDataSetChanged();

        });


    }

    private void initViews() {
        BottomNavigationView navView = activityMainBinding.navView;
        Menu menu = navView.getMenu();
        MenuItem menuItemHome = menu.findItem(R.id.navigation_home);

        state = BaseActivity.state;
        knownName = BaseActivity.knownName;
        country = BaseActivity.country;


        recyclerView = activityMainBinding.recyclerViewHome;
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new HomeRecyclerAdapter(arrayList, this);
        recyclerView.setAdapter(adapter);

        navView.setOnNavigationItemReselectedListener(item -> {

        });


        BadgeDrawable badgeDrawableHome = navView.getOrCreateBadge(menuItemHome.getItemId());
        runOnUiThread(() -> alertsCollectionReference
                .whereEqualTo("isSolved", false)
                .get().addOnCompleteListener(task -> {

                    if (task.getResult().size() > 0)
                        badgeDrawableHome.setNumber(task.getResult().size());

                }));


    }


    void myIntent() {

        Intent intent = new Intent(this, ReportActivity.class);
        intent.putExtra(AppConstants.PHONE_NUMBER, phoneNumber);
        intent.putExtra(AppConstants.USER_PHOTO_URL, userPhotoUrl);
        intent.putExtra(AppConstants.USER_NAME, userName);
        intent.putExtra(AppConstants.UID, userId);

        startActivity(intent);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.switch_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.reportMenu) {
            if (!checkPermissions()) {
                requestPermissions();
            } else {
                mService.requestLocationUpdates();
            }
            // Restore the state of the buttons when the activity (re)launches.
            setButtonsState(Utils.requestingLocationUpdates(this));

            // Bind to the service. If the service is in foreground mode, this signals to the service
            // that since this activity is in the foreground, the service can exit foreground mode.
            bindService(new Intent(this, LocationUpdatesService.class), mServiceConnection,
                    Context.BIND_AUTO_CREATE);
            // myIntent();
        } else if (item.getItemId() == R.id.navigation_maps) {
            startActivity(new Intent(this, MapsActivity.class));
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
        // Restore the state of the buttons when the activity (re)launches.
        setButtonsState(Utils.requestingLocationUpdates(this));


        if (!checkPermissions()) {
            requestPermissions();
        } else {
//            mService.requestLocationUpdates();
        }

        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        bindService(new Intent(this, LocationUpdatesService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));

        if (mBundleState != null) {

            new Handler().postDelayed(() -> {

                mState = mBundleState.getParcelable(KEY);
                layoutManager.onRestoreInstanceState(mState);
            }, 50);
        }

        recyclerView.setLayoutManager(layoutManager);

    }

    @Override
    protected void onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection);
            mBound = false;
        }
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        registration.remove();

    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        super.onPause();
        mBundleState = new Bundle();
        mState = layoutManager.onSaveInstanceState();
        mBundleState.putParcelable(KEY, mState);
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mService.requestLocationUpdates();

            } else {

                requestPermissions();


            }
        }
    }


    /**
     * Returns the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
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
                                REQUEST_PERMISSIONS_REQUEST_CODE);
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    void getAddress(Location mLocation) {

        try {
            List<Address> addressList = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);

            if (addressList != null) {
                address = addressList.get(0).getAddressLine(0);
                state = addressList.get(0).getAdminArea();
                country = addressList.get(0).getCountryName();
                knownName = addressList.get(0).getFeatureName();

                Toast.makeText(this, "Address--" + address
                                + " state--" + state + " country--" + country + " known name" + knownName
                        , Toast.LENGTH_LONG).show();

                Log.i(TAG, String.format(Locale.ENGLISH, "%s: %f", "lat",
                        mLocation.getLatitude()));
                Log.i(TAG, String.format(Locale.ENGLISH, "%s: %f", "lng",
                        mLocation.getLongitude()));
                Log.i(TAG, String.format(Locale.ENGLISH, "%s: %s",
                        "Known Name", knownName));

                Log.i(TAG, String.format(Locale.ENGLISH, "%s: %s",
                        "address ", address));
                Log.i(TAG, String.format(Locale.ENGLISH, "%s: %s",
                        "state ", state));
                Log.i(TAG, String.format(Locale.ENGLISH, "%s: %s",
                        "country ", country));


            }


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        // Update the buttons state depending on whether location updates are being requested.
        if (s.equals(Utils.KEY_REQUESTING_LOCATION_UPDATES)) {
            setButtonsState(sharedPreferences.getBoolean(Utils.KEY_REQUESTING_LOCATION_UPDATES,
                    false));
        }
    }

    private void setButtonsState(boolean requestingLocationUpdates) {
        if (requestingLocationUpdates) {

            Log.i(TAG, "true: ");
        } else {

            Log.i(TAG, "false: ");
        }
    }

    /**
     * Receiver for broadcasts sent by {@link LocationUpdatesService}.
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            if (location != null) {
                getAddress(location);
               /* Toast.makeText(MainActivity.this, Utils.getLocationText(location),
                        Toast.LENGTH_SHORT).show();*/
            }
        }
    }

}