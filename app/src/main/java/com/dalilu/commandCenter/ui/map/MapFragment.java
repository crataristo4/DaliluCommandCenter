package com.dalilu.commandCenter.ui.map;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.dalilu.commandCenter.R;
import com.dalilu.commandCenter.databinding.FragmentMapBinding;
import com.dalilu.commandCenter.model.AlertItems;
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

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private final CollectionReference collectionReference = FirebaseFirestore
            .getInstance()
            .collection("Alerts");
    private GoogleMap mMap;
    private MarkerOptions marker;
    private static final String TAG = "MapsActivity";
    private static final LatLngBounds MALI = new LatLngBounds( new LatLng(10.0963607854, -12.1707502914),
            new LatLng(24.9745740829, 4.27020999514));

    FragmentMapBinding fragmentMapBinding;
    SupportMapFragment mapFragment;

    public MapFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,

                             ViewGroup container, Bundle savedInstanceState) {
        fragmentMapBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);
        return fragmentMapBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
            mapFragment.setRetainInstance(true);

        }

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {

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
                    LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());

                    String dot = String.valueOf(Html.fromHtml("&#9673;"));

                    if (!isSolved) {
                        marker = new MarkerOptions().position(latLng)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                .title(userName + " " + dot + " " + GetTimeAgo.getTimeAgo(timeStampX))
                                .snippet(address + " " + dot + " " + dateReported);
                    } else {
                        marker = new MarkerOptions().position(latLng)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                .title(userName + " " + dot + " " + GetTimeAgo.getTimeAgo(timeStampX))
                                .snippet("Seen by police " + " " + dot);
                    }

                    mMap.addMarker(marker);

             /*   mMap.setOnMarkerClickListener(marker -> {

                    if (marker != null) {

                        String id1 = marker.getSnippet();
                        DisplayViewUI.displayToast(requireActivity(), id1);

                    }


                    return false;
                });*/


                }


            });


        } catch (Exception e) {
            e.printStackTrace();

        }

    }


}