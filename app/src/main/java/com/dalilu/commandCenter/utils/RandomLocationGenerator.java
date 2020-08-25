package com.dalilu.commandCenter.utils;


import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.Random;

public final class RandomLocationGenerator {

    private static final Random RANDOM = new Random();

    @NonNull
    public   static LatLng generate(@NonNull LatLngBounds bounds) {
        double minLatitude = bounds.southwest.latitude;
        double maxLatitude = bounds.northeast.latitude;
        double minLongitude = bounds.southwest.longitude;
        double maxLongitude = bounds.northeast.longitude;
        return new LatLng(
                minLatitude + (maxLatitude - minLatitude) * RANDOM.nextDouble(),
                minLongitude + (maxLongitude - minLongitude) * RANDOM.nextDouble());
    }

    private RandomLocationGenerator() {
    }
}
