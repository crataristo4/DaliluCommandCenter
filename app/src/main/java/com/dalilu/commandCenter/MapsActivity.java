package com.dalilu.commandCenter;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.dalilu.commandCenter.model.AlertItems;
import com.dalilu.commandCenter.utils.DisplayViewUI;
import com.dalilu.commandCenter.utils.GetTimeAgo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.MessageFormat;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private final CollectionReference collectionReference = FirebaseFirestore
            .getInstance()
            .collection("Alerts");
    private GoogleMap mMap;
    private MarkerOptions marker;
    private static final String TAG = "MapsActivity";
    private static final LatLngBounds MALI = new LatLngBounds(new LatLng(10.0963607854, -12.1707502914),
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


    }


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

                LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                String dot = String.valueOf(Html.fromHtml("&#9673;"));
                String seen = "Seen by police " + " " + dot;

                if (!isSolved) {
                    marker = new MarkerOptions().position(latLng)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            .title(userName + " " + dot + " " + GetTimeAgo.getTimeAgo(timeStampX))
                            .snippet(address + " " + dot + " " + dateReported);


                } else {
                    marker = new MarkerOptions().position(latLng)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                            .title(userName + " " + dot + " " + GetTimeAgo.getTimeAgo(timeStampX))
                            .snippet(seen);

                }
                mMap.addMarker(marker);

                mMap.setOnMarkerClickListener(marker -> {

                    if (marker.getSnippet().equals(seen)) {

                        DisplayViewUI.displayAlertDialog(this,
                                seen,
                                "Case has been seen by the Police",
                                "OK",

                                (dialogInterface, i) -> {

                                    dialogInterface.dismiss();

                                });

                    } else {

                        DisplayViewUI.displayAlertDialog(MapsActivity.this,
                                "Case NOT seen",
                                MessageFormat.format("Posted by : {0}\n\n"
                                                + "Location and Details : {1}\n\n" +
                                                "This case has not been seen by the police!",
                                        marker.getTitle(), marker.getSnippet()),
                                "FOLLOW UP",
                                (dialogInterface, i) -> dialogInterface.dismiss()

                        );


                    }

                    return false;


                });

            }


        });

    }

}