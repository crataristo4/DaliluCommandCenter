package com.dalilu.commandCenter.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.dalilu.commandCenter.R;
import com.dalilu.commandCenter.databinding.FragmentMapBinding;
import com.dalilu.commandCenter.model.AlertItems;
import com.dalilu.commandCenter.utils.AppConstants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class ProfileFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "Map Frag";
    FragmentMapBinding fragmentMapBinding;
    private final CollectionReference collectionReference = FirebaseFirestore
            .getInstance()
            .collection("Alerts");
    private final ArrayList<LatLng> arrayList = new ArrayList<>();
    ListenerRegistration registration;
    private GoogleMap mMap;
    MarkerOptions options = new MarkerOptions();
    private float mapZoomLevel;

    public ProfileFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,

                             ViewGroup container, Bundle savedInstanceState) {
        fragmentMapBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);
        return fragmentMapBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        mapZoomLevel = 13;




    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;



        Query query = collectionReference.orderBy("timeStamp");
        registration  = query.addSnapshotListener((queryDocumentSnapshots, e) -> {
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
                Log.i(TAG, "Lat: " + geoPoint.getLatitude() + " Lng: " + geoPoint.getLongitude());

                arrayList.add(new LatLng(geoPoint.getLatitude(),geoPoint.getLongitude()));

                for (LatLng point : arrayList){

                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    mMap.getUiSettings().setZoomGesturesEnabled(true);
                    mMap.getUiSettings().setCompassEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    mMap.getUiSettings().setRotateGesturesEnabled(true);

                    options.position(point);
                    mMap.addMarker(options);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, mapZoomLevel));


                }


            }



        });

    }


    private void fetchData() {



// Create a query against the collection.

    }
}