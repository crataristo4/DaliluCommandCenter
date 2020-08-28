package com.dalilu.commandCenter;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.dalilu.commandCenter.model.AlertItems;
import com.dalilu.commandCenter.utils.GetTimeAgo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private final CollectionReference collectionReference = FirebaseFirestore
            .getInstance()
            .collection("Alerts");
    private GoogleMap mMap;
    private MarkerOptions marker;
    private static final String TAG = "MapsActivity";
    private static final LatLngBounds MALI = new LatLngBounds( new LatLng(10.0963607854, -12.1707502914),
            new LatLng(24.9745740829, 4.27020999514));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        /*if (savedInstanceState == null) {
            setupMapFragment();
        }*/

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.setOnMapLoadedCallback(() -> mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(MALI, 0)));



        Query query = collectionReference.orderBy("timeStamp");
        query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            // arrayList.clear();
            assert queryDocumentSnapshots != null;
            for (QueryDocumentSnapshot ds : queryDocumentSnapshots) {

                AlertItems alertItems = ds.toObject(AlertItems.class);
                //get data from model
                GeoPoint geoPoint = alertItems.getCoordinates();
                String address = alertItems.getAddress();
                String userName = alertItems.getUserName();
                String phoneNumber = alertItems.getPhoneNumber();
                long timeStampX = alertItems.getTimeStamp();
                String userPhotoUrl = alertItems.getUserPhotoUrl();
                String url = alertItems.getUrl();
                boolean isSolved = alertItems.isSolved();

                String id = ds.getId();
                String dateReported = alertItems.getDateReported();
                Log.i(TAG, "Lat: " + geoPoint.getLatitude() + " Lng: " + geoPoint.getLongitude());

                LatLng latLng = new LatLng(geoPoint.getLatitude(),geoPoint.getLongitude());

                String dot = String.valueOf(Html.fromHtml("&#9673;"));

                marker = new MarkerOptions().position(latLng)
                        .title(userName + " " + dot +  " " + GetTimeAgo.getTimeAgo(timeStampX))
                        .snippet(dateReported);

                mMap.addMarker(marker);


/*
                for (LatLng point : arrayList){

                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    mMap.getUiSettings().setZoomGesturesEnabled(true);
                    mMap.getUiSettings().setCompassEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    mMap.getUiSettings().setRotateGesturesEnabled(true);

                    mMap.addMarker(new MarkerOptions().position(point).title(userName).snippet(dateReported));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point,11));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, mapZoomLevel));

                   */






            }



        });




    }


    //Map Clustering
   /* @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(NETHERLANDS, 0));
            }
        });

        ClusterManager<SampleClusterItem> clusterManager = new ClusterManager<>(this, googleMap);
        clusterManager.setCallbacks(new ClusterManager.Callbacks<SampleClusterItem>() {
            @Override
            public boolean onClusterClick(@NonNull Cluster<SampleClusterItem> cluster) {
                Log.d(TAG, "onClusterClick");
                return false;
            }

            @Override
            public boolean onClusterItemClick(@NonNull SampleClusterItem clusterItem) {
                Log.d(TAG, "onClusterItemClick");
                return false;
            }
        });
        googleMap.setOnCameraIdleListener(clusterManager);

        List<SampleClusterItem> clusterItems = new ArrayList<>();
        for (int i = 0; i < 20000; i++) {
            clusterItems.add(new SampleClusterItem(
                    RandomLocationGenerator.generate(NETHERLANDS)));
        }
        clusterManager.setItems(clusterItems);
    }

    private void setupMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.setRetainInstance(true);
        mapFragment.getMapAsync(this);
    }*/
}