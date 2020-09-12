package com.dalilu.commandCenter.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalilu.commandCenter.MapsActivity;
import com.dalilu.commandCenter.R;
import com.dalilu.commandCenter.adapters.HomeRecyclerAdapter;
import com.dalilu.commandCenter.databinding.ActivityMainBinding;
import com.dalilu.commandCenter.model.AlertItems;
import com.dalilu.commandCenter.utils.AppConstants;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

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
    public static double latitude, longitude;
    private CollectionReference alertsCollectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

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
        getMenuInflater().inflate(R.menu.switch_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.reportMenu) {
            myIntent();
        } else if (item.getItemId() == R.id.navigation_maps) {
            startActivity(new Intent(this, MapsActivity.class));
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mBundleState != null) {

            new Handler().postDelayed(() -> {

                mState = mBundleState.getParcelable(KEY);
                layoutManager.onRestoreInstanceState(mState);
            }, 50);
        }

        recyclerView.setLayoutManager(layoutManager);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        registration.remove();

    }

    @Override
    public void onPause() {
        super.onPause();
        mBundleState = new Bundle();
        mState = layoutManager.onSaveInstanceState();
        mBundleState.putParcelable(KEY, mState);
    }


}